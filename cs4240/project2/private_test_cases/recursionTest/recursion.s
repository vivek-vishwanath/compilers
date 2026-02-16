.data
	STACK: .word -2147483648

.text
li $t0, 0
li $t1, 0
li $t2, 0
li $t3, 0
li $t4, 0
li $t5, 0
li $t6, 0
li $t7, 0
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
		move $a0, $t0
		addi $sp, $sp, -4
		sw $t0, 0($sp)
		jal factorial
		lw $t0, 0($sp)
		addi $sp, $sp, 4
		move $t0, $v0
		li $v0, 1
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
		move $t1, $a0
		li $t0, 1
		bgt $t1, $t0, factorial_recursive_step
factorial_base_case:
		li $v0, 1
		j factorial_teardown
factorial_recursive_step:
		li $t2, 1
		sub $t0, $t1, $t2
		move $a0, $t0
		addi $sp, $sp, -8
		sw $t1, 4($sp)
		sw $t2, 0($sp)
		jal factorial
		lw $t1, 4($sp)
		lw $t2, 0($sp)
		addi $sp, $sp, 8
		move $t2, $v0
		mul $t0, $t1, $t2
		move $v0, $t0
		j factorial_teardown
factorial_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

