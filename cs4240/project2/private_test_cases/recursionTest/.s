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
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 4($sp)
		lw $t0, 4($sp)
		move $a0, $t0
		jal factorial
		move $t0, $v0
		sw $t0, 0($sp)
		li $v0, 1
		lw $t0, 0($sp)
		move $a0, $t0
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

factorial:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -16
		move $t0, $a0
		sw $t0, 12($sp)
		li $t0, 1
		sw $t0, 8($sp)
		lw $t0, 12($sp)
		lw $t1, 8($sp)
		bgt $t0, $t1, factorial_recursive_step
factorial_base_case:
		li $v0, 1
		j factorial_teardown
factorial_recursive_step:
		li $t0, 1
		sw $t0, 8($sp)
		lw $t0, 12($sp)
		lw $t1, 8($sp)
		sub $t0, $t0, $t1
		sw $t0, 4($sp)
		lw $t0, 4($sp)
		move $a0, $t0
		jal factorial
		move $t0, $v0
		sw $t0, 0($sp)
		lw $t0, 12($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, -4($sp)
		lw $t0, -4($sp)
		move $v0, $t0
		j factorial_teardown
factorial_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

