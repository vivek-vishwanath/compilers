#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Global trace file
static FILE *trace_file = NULL;
static const char *trace_filename = "program_trace.txt";

// Initialize tracing - open trace file
void __trace_init() {
    trace_file = fopen(trace_filename, "w");
}

// Finalize tracing - close trace file
void __trace_finalize() {
    if (trace_file) {
        fclose(trace_file);
        trace_file = NULL;
        printf("Trace written to %s\n", trace_filename);
    }
}

// Record function entry
void __trace_function_entry(const char *function_name) {
    if (trace_file) {
        fprintf(trace_file, ">%s\n", function_name);
    }
}

// Record function exit
void __trace_function_exit(const char *function_name) {
    if (trace_file) {
        fprintf(trace_file, "<%s\n", function_name);
    }
}
