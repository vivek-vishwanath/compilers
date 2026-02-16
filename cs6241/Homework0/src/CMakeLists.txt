set(PASS_MODULE HW0)
add_library(${PASS_MODULE} MODULE Pass.cpp CounterAnalysis.cpp)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  target_link_options(${PASS_MODULE} BEFORE PRIVATE -undefined dynamic_lookup)
endif()

install(TARGETS ${PASS_MODULE} DESTINATION lib/ )
