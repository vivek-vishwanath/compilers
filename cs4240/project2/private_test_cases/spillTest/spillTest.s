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
		addi $sp, $fp, -52
		li $t8, 0
		sw $t8, 48($sp)
		sw $t8, 48($sp)
		li $t8, 1
		sw $t8, 44($sp)
		sw $t8, 44($sp)
		li $t8, 2
		sw $t8, 40($sp)
		sw $t8, 40($sp)
		li $t8, 3
		sw $t8, 36($sp)
		sw $t8, 36($sp)
		lw $t8, 36($sp)
		lw $t9, 36($sp)
		lw $t8, 36($sp)
		sw $t8, 36($sp)
		lw $t9, 36($sp)
		lw $t9, 36($sp)
		lw $t9, 36($sp)
		add $t8, $t9, $t9
		sw $t8, 36($sp)
		li $t8, 4
		sw $t8, 32($sp)
		sw $t8, 32($sp)
		li $t7, 5
		li $t6, 6
		li $t5, 7
		li $t4, 8
		li $t3, 9
		li $t2, 10
		li $t1, 11
		li $t0, 12
		lw $t8, 32($sp)
		lw $t9, 32($sp)
		lw $t8, 32($sp)
		sw $t8, 32($sp)
		lw $t9, 32($sp)
		lw $t9, 32($sp)
		lw $t9, 32($sp)
		add $t8, $t9, $t9
		sw $t8, 32($sp)
		add $t7, $t7, $t7
		add $t6, $t6, $t6
		add $t5, $t5, $t5
		add $t4, $t4, $t4
		add $t3, $t3, $t3
		add $t2, $t2, $t2
		add $t1, $t1, $t1
		add $t0, $t0, $t0
		lw $t8, 44($sp)
		lw $t9, 40($sp)
		lw $t8, 44($sp)
		sw $t8, 48($sp)
		lw $t9, 40($sp)
		add $t8, $t8, $t9
		sw $t8, 48($sp)
		li $v0, 1
		lw $t8, 48($sp)
		lw $t8, 48($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 44($sp)
		lw $t8, 44($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 40($sp)
		lw $t8, 40($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 36($sp)
		lw $t8, 36($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 32($sp)
		lw $t8, 32($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t7
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t6
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t5
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t4
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t3
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t1
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		move $a0, $t0
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

