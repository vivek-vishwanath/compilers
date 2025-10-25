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
		li $t4, 0
		sw $t4, 0($sp)
		li $t4, 100
		sw $t4, 4($sp)
main_start_loop:
		lw $t6, 4($sp)
		lw $t4, 0($sp)
		bge $t4, $t6, main_exit_loop
		lw $t4, 0($sp)
		addi $t4, $t4, 1
		sw $t4, 0($sp)
		j main_start_loop
main_exit_loop:
		li $v0, 1
		lw $t4, 0($sp)
		move $a0, $t4
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

