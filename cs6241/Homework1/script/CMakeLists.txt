set(DEF ${CMAKE_CURRENT_BINARY_DIR}/def)
file(WRITE ${DEF} "#!/bin/bash\n" "CMAKE_SRC=\"${CMAKE_SOURCE_DIR}\"")
file(CHMOD ${DEF} PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
install(PROGRAMS run_pass.sh run_pass_and_compile.sh ${DEF} DESTINATION .)