set(PASS_MODULE HW1)

add_library(${PASS_MODULE} MODULE
  HW1Registry.cpp
  Part020.cpp
  Part031.cpp
  Part032.cpp
  Part033.cpp
  Part034.cpp
  Part040.cpp
)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  target_link_options(${PASS_MODULE} BEFORE PRIVATE -undefined dynamic_lookup)
endif()
# Enable debug symbols if required:
# target_compile_options(${PASS_MODULE} PRIVATE -g)
install(TARGETS ${PASS_MODULE} DESTINATION .)
