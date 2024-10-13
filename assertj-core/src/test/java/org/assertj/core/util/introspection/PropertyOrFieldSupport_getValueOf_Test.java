/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2024 the original author or authors.
 */
package org.assertj.core.util.introspection;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.introspection.FieldSupport.EXTRACTION_OF_PUBLIC_FIELD_ONLY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.testkit.Employee;
import org.assertj.core.testkit.Name;
import org.assertj.core.testkit.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyOrFieldSupport_getValueOf_Test {

  private final PropertyOrFieldSupport underTest = PropertyOrFieldSupport.EXTRACTION;
  private final Employee yoda = new Employee(1L, new Name("Yoda"), 800);
  private final Employee luke = new Employee(3L, new Name("Luke", "Skywalker"), 24);
  private final Employee han = new Employee(3L, new Name("Han"), 31); 
  private final Office office = new Office();
  private final Office officeNearby = new Office();

  @BeforeEach
  void setup() {
    yoda.setRelation("padawan", luke);
    office.addEmployees(asList(yoda, luke));
    officeNearby.addEmployee(han);
    office.addNearbyOffice(officeNearby);
  }

  @Test
  void should_extract_property_value() {
    // WHEN
    Object value = underTest.getValueOf("age", yoda);
    // THEN
    then(value).isEqualTo(800);
  }

  @Test
  void should_extract_property_with_no_corresponding_field() {
    // WHEN
    Object value = underTest.getValueOf("adult", yoda);
    // THEN
    then(value).isEqualTo(true);
  }

  @Test
  void should_prefer_properties_over_fields() {
    // WHEN
    Object extractedValue = underTest.getValueOf("name", employeeWithOverriddenName("Overridden Name"));
    // THEN
    then(extractedValue).isEqualTo(new Name("Overridden Name"));
  }

  @Test
  void should_extract_public_field_values_as_no_property_matches_given_name() {
    // WHEN
    Object value = underTest.getValueOf("id", yoda);
    // THEN
    then(value).isEqualTo(1L);
  }

  @Test
  void should_extract_private_field_values_as_no_property_matches_given_name() {
    // WHEN
    Object value = underTest.getValueOf("city", yoda);
    // THEN
    then(value).isEqualTo("New York");
  }

  @Test
  void should_fallback_to_field_if_exception_has_been_thrown_on_property_access() {
    // WHEN
    Object extractedValue = underTest.getValueOf("name", employeeWithBrokenName("Name"));
    // THEN
    then(extractedValue).isEqualTo(new Name("Name"));
  }

  @Test
  void should_return_null_if_one_of_nested_property_or_field_value_is_null() {
    // WHEN
    Object value = underTest.getValueOf("surname.first", yoda);
    // THEN
    then(value).isNull();
  }

  @Test
  void should_extract_nested_property_field_combinations() {
    // GIVEN
    Employee darth = new Employee(1L, new Name("Darth", "Vader"), 100);
    Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
    darth.field = luke;
    luke.field = darth;
    luke.surname = new Name("Young", "Padawan");
    // WHEN
    Object value = underTest.getValueOf("me.field.me.field.me.field.surname.name", darth);
    // THEN
    then(value).isEqualTo("Young Padawan");
  }

  @Test
  void should_extract_value_from_nested_map() {
    // GIVEN
    Employee darth = new Employee(1L, new Name("Darth", "Vader"), 100);
    darth.setAttribute("side", "dark");
    // WHEN
    Object value = underTest.getValueOf("attributes.side", darth);
    // THEN
    then(value).isEqualTo("dark");
  }

  @Test
  void should_extract_nested_property_field_within_nested_map() {
    // WHEN
    Object value = underTest.getValueOf("relations.padawan.name.first", yoda);
    // THEN
    then(value).isEqualTo("Luke");
  }

  @Test
  void should_throw_error_when_no_property_nor_field_match_given_name() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("unknown", yoda));
    // THEN
    then(thrown).isInstanceOf(IntrospectionError.class);
  }

  @Test
  void should_throw_error_when_no_property_nor_public_field_match_given_name_if_extraction_is_limited_to_public_fields() {
    // GIVEN
    PropertyOrFieldSupport underTest = new PropertyOrFieldSupport(new PropertySupport(), EXTRACTION_OF_PUBLIC_FIELD_ONLY);
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("city", yoda));
    // THEN
    then(thrown).isInstanceOf(IntrospectionError.class);
  }

  @Test
  void should_throw_exception_when_given_property_or_field_name_is_null() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf(null, yoda));
    // THEN
    then(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The name of the property/field to read should not be null");
  }

  @Test
  void should_throw_exception_when_given_name_is_empty() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("", yoda));
    // THEN
    then(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The name of the property/field to read should not be empty");
  }

  @Test
  void should_throw_exception_if_property_cannot_be_extracted_due_to_runtime_exception_during_property_access() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("adult", brokenEmployee()));
    // THEN
    then(thrown).isInstanceOf(IntrospectionError.class);
  }

  @Test
  void should_throw_exception_if_no_object_is_given() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("name", null));
    // THEN
    then(thrown).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_extract_single_value_from_maps_by_key() {
    String key1 = "key1";
    String key2 = "key2";
    String keyWithNullValue = "key with null";
    Map<String, Employee> map1 = new HashMap<>();
    map1.put(key1, yoda);
    Employee luke = new Employee(2L, new Name("Luke"), 22);
    map1.put(key2, luke);
    map1.put(keyWithNullValue, null);

    Map<String, Employee> map2 = new HashMap<>();
    map2.put(key1, yoda);
    Employee han = new Employee(3L, new Name("Han"), 31);
    map2.put(key2, han);
    map2.put(keyWithNullValue, null);

    List<Map<String, Employee>> maps = asList(map1, map2);
    assertThat(maps).extracting(key2).containsExactly(luke, han);
    assertThat(maps).extracting(key2, Employee.class).containsExactly(luke, han);
    assertThat(maps).extracting(key1).containsExactly(yoda, yoda);
    assertThat(maps).extracting(keyWithNullValue).containsExactly(null, null);

    assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> assertThat(maps).extracting("bad key"));
  }

  @Test
  void should_extract_field_value_if_only_static_getter_matches_name() {
    // WHEN
    Object value = underTest.getValueOf("city", new StaticPropertyEmployee());
    // THEN
    then(value).isEqualTo("New York");
  }

  @Test
  void should_extract_field_value_if_only_static_is_method_matches_name() {
    // WHEN
    Object value = underTest.getValueOf("tall", new StaticBooleanPropertyEmployee());
    // THEN
    then(value).isEqualTo(false);
  }

  @Test
  void should_extract_field_value_if_only_static_bare_method_matches_name() {
    // WHEN
    Object value = underTest.getValueOf("city", new StaticBarePropertyEmployee());
    // THEN
    then(value).isEqualTo("New York");
  }

  @Test
  void should_extract_field_value_if_only_void_method_matches_name() {
    // WHEN
    Object value = underTest.getValueOf("city", new VoidGetterPropertyEmployee());
    // THEN
    then(value).isEqualTo("New York");
  }

  @Test
  void should_extract_property_value_with_index_if_list() {
    // WHEN
    Object value = underTest.getValueOf("employees[0]", office);
    // THEN
    then(value).isEqualTo(yoda);
  }

  @Test
  void should_extract_nested_property_value_with_indexes_if_lists() {
    // WHEN
    Object value = underTest.getValueOf("nearbyOffices[0].employees[0]", office);
    // THEN
    then(value).isEqualTo(han);
  }


  @Test
  void should_extract_property_value_with_index_if_array() {
    // WHEN
    Object value = underTest.getValueOf("employeesArray[0]", office);
    // THEN
    then(value).isEqualTo(yoda);
  }

  @Test
  void should_throw_error_when_not_an_array_or_list() {
    // WHEN
    Throwable thrown = catchThrowable(() -> underTest.getValueOf("office", office));
    // THEN
    then(thrown).isInstanceOf(IntrospectionError.class);
  }

  @Test
  void should_extract_single_value_from_maps_by_key_even_if_key_has_index() {
    // GIVEN
    String key = "employees[0]";
    Map<String, Employee> map = new HashMap<>(1);
    map.put(key, yoda);
    // WHEN
    Object value = underTest.getValueOf("employees[0]", map);
    // THEN
    then(value).isEqualTo(yoda);
  }

  @Test
  void should_extract_single_value_from_maps_by_key_and_index_when_value_is_list() {
    // GIVEN
    String key = "employees";
    Map<String, List<Employee>> map = new HashMap<>(1);
    map.put(key, asList(yoda));
    // WHEN
    Object value = underTest.getValueOf("employees[0]", map);
    // THEN
    then(value).isEqualTo(yoda);
  }

  
  @Test
  void should_extract_single_value_from_maps_by_key_and_index_when_value_is_array() {
    // GIVEN
    String key = "employees";
    Map<String, Employee[]> map = new HashMap<>();
    map.put(key, array(yoda));
    // WHEN
    Object value = underTest.getValueOf("employees[0]", map);
    // THEN
    then(value).isEqualTo(yoda);
  }

  @Test
  void should_extract_single_value_from_maps_by_key_even_if_key_has_broken_index() {
    // GIVEN
    String key0 = "employees[";
    String key1 = "employees[foo";
    String key2 = "employees]";
    Employee bar = new Employee();
    Map<String, Employee> map = new HashMap<>();
    map.put(key0, yoda);
    map.put(key1, luke);
    map.put(key2, bar);
    // WHEN
    Object value0 = underTest.getValueOf("employees[", map);
    Object value1 = underTest.getValueOf("employees[foo", map);
    Object value2 = underTest.getValueOf("employees]", map);
    // THEN
    then(value0).isEqualTo(yoda);
    then(value1).isEqualTo(luke);
    then(value2).isEqualTo(bar);
  }

  private Employee employeeWithBrokenName(String name) {
    return new Employee(1L, new Name(name), 0) {
      @Override
      public Name getName() {
        throw new IllegalStateException();
      }
    };
  }

  private Employee employeeWithOverriddenName(final String overriddenName) {
    return new Employee(1L, new Name("Name"), 0) {
      @Override
      public Name getName() {
        return new Name(overriddenName);
      }
    };
  }

  private Employee brokenEmployee() {
    return new Employee() {
      @Override
      public boolean isAdult() {
        throw new IllegalStateException();
      }
    };
  }

  static class StaticPropertyEmployee extends Employee {
    public static String getCity() {
      return "London";
    }
  }

  static class StaticBarePropertyEmployee extends Employee {
    public static String city() {
      return "London";
    }
  }

  static class StaticBooleanPropertyEmployee extends Employee {
    @SuppressWarnings("unused")
    boolean tall = false;

    public static boolean isTall() {
      return true;
    }
  }

  static class VoidGetterPropertyEmployee extends Employee {
    public void getCity() {}
  }

}
