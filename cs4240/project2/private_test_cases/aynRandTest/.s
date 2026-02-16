.data
	STACK: .word -2147483648

.text
	lw $sp, STACK
	move $fp, $sp
	jal main
	li $v0, 10
	syscall

main:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -8
		li $t0, 0
		sw $t0, 4($sp)
		li $t0, 100
		sw $t0, 0($sp)
main_start_loop:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bge $t0, $t1, main_exit_loop
		lw $t0, 4($sp)
		addi $t0, $t0, 1
		sw $t0, 4($sp)
		j main_start_loop
main_exit_loop:
		li $v0, 1
		lw $t0, 4($sp)
		move $a0, $t0
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

