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
		addi $sp, $fp, -56
		li $t8, 0
		sw $t8, 52($sp)
		sw $t8, 52($sp)
		li $t8, 1
		sw $t8, 48($sp)
		sw $t8, 48($sp)
		li $t8, 2
		sw $t8, 44($sp)
		sw $t8, 44($sp)
		li $t8, 3
		sw $t8, 40($sp)
		sw $t8, 40($sp)
		li $t8, 4
		sw $t8, 36($sp)
		sw $t8, 36($sp)
		li $t8, 5
		sw $t8, 32($sp)
		sw $t8, 32($sp)
		li $t7, 6
		li $t6, 7
		li $t5, 8
		li $t4, 9
		li $t3, 10
		li $t2, 11
		li $t1, 12
		addi $sp, $sp, -28
		sw $t3, 24($sp)
		sw $t7, 20($sp)
		sw $t4, 16($sp)
		sw $t2, 12($sp)
		sw $t5, 8($sp)
		sw $t1, 4($sp)
		sw $t6, 0($sp)
		jal foo
		lw $t3, 24($sp)
		lw $t7, 20($sp)
		lw $t4, 16($sp)
		lw $t2, 12($sp)
		lw $t5, 8($sp)
		lw $t1, 4($sp)
		lw $t6, 0($sp)
		addi $sp, $sp, 28
		move $t0, $v0
		li $v0, 1
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 52($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 48($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 44($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 40($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 36($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
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
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

foo:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -44
		li $t8, 11
		sw $t8, 40($sp)
		sw $t8, 40($sp)
		li $t8, 12
		sw $t8, 36($sp)
		sw $t8, 36($sp)
		li $t8, 13
		sw $t8, 32($sp)
		sw $t8, 32($sp)
		li $t7, 14
		li $t6, 15
		li $t5, 16
		li $t4, 17
		li $t3, 18
		li $t2, 19
		li $t1, 20
		li $t0, 21
		li $v0, 1
		lw $t8, 40($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t8, 36($sp)
		move $a0, $t8
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
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
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 4240
		j foo_teardown
foo_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

