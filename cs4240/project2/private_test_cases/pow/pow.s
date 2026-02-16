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
		addi $sp, $fp, -12
		li $v0, 5
		syscall
		move $t1, $v0
		li $v0, 5
		syscall
		move $t0, $v0
		move $a0, $t1
		move $a1, $t0
		addi $sp, $sp, -4
		sw $t0, 0($sp)
		jal pow
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

pow:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -12
		move $t3, $a0
		move $t2, $a1
		li $t1, 1
		li $t0, 0
		beq $t2, $t0, pow_return1
		li $t0, 0
		beq $t3, $t0, pow_return0
pow_loop:
		mul $t1, $t1, $t3
		li $t0, 1
		sub $t2, $t2, $t0
		li $t0, 0
		bgt $t2, $t0, pow_loop
		move $v0, $t1
		j pow_teardown
pow_return0:
		li $v0, 0
		j pow_teardown
pow_return1:
		li $v0, 1
		j pow_teardown
pow_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

