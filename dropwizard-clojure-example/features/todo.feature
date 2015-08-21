Feature: Create a todo

Scenario: Add a todo
  Given The todo list is reset
  Then The todo list contains 0 entries
  When A todo is created
  Then The todo list contains 1 entries