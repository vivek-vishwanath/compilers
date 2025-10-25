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
		addi $sp, $fp, -12
		li $a0, 3
		li $v0, 9
		syscall
		move $t6, $v0
		sw $t6, 8($sp)
		li $t4, 4240
		lw $t6, 8($sp)
		sw $t4, 0($t6)
		li $t4, 4240
		sw $t4, 4($t6)
		li $t4, 4240
		sw $t4, 8($t6)
		li $t6, 0
		sw $t6, 4($sp)
main_start_loop1:
		li $t4, 3
		lw $t6, 4($sp)
		bge $t6, $t4, main_exit_loop1
		li $v0, 12
		syscall
		move $t6, $v0
		sw $t6, 0($sp)
		li $t4, 4
		lw $t6, 4($sp)
		mul $t4, $t6, $t4
		lw $t0, 8($sp)
		add $t4, $t0, $t4
		lw $t0, 0($sp)
		sw $t0, ($t4)
		addi $t6, $t6, 1
		sw $t6, 4($sp)
		j main_start_loop1
main_exit_loop1:
		li $t6, 2
		sw $t6, 4($sp)
main_start_loop2:
		li $t6, 0
		lw $t4, 4($sp)
		blt $t4, $t6, main_exit_loop2
		li $t6, 4
		lw $t4, 4($sp)
		mul $t6, $t4, $t6
		lw $t4, 8($sp)
		add $t6, $t4, $t6
		lw $t6, ($t6)
		sw $t6, 0($sp)
		li $v0, 11
		lw $t6, 0($sp)
		move $a0, $t6
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		lw $t6, 4($sp)
		addi $t6, $t6, -1
		sw $t6, 4($sp)
		j main_start_loop2
main_exit_loop2:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

