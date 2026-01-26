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
		addi $sp, $fp, -28
		li $t0, 123
		sw $t0, 24($sp)
		li $t0, 456
		sw $t0, 20($sp)
		li $t0, 789
		sw $t0, 16($sp)
		li $t0, -123
		sw $t0, 12($sp)
		li $t0, -456
		sw $t0, 8($sp)
		li $t0, -789
		sw $t0, 4($sp)
		lw $t0, 8($sp)
		sw $t0, -16($sp)
		lw $t0, 4($sp)
		sw $t0, -12($sp)
		lw $t0, 24($sp)
		move $a0, $t0
		lw $t0, 20($sp)
		move $a1, $t0
		lw $t0, 16($sp)
		move $a2, $t0
		lw $t0, 12($sp)
		move $a3, $t0
		jal foo
		move $t0, $v0
		sw $t0, 0($sp)
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

foo:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -36
		move $t0, $a0
		sw $t0, 24($sp)
		move $t0, $a1
		sw $t0, 20($sp)
		move $t0, $a2
		sw $t0, 16($sp)
		move $t0, $a3
		sw $t0, 12($sp)
		lw $t0, 28($sp)
		sw $t0, -16($sp)
		lw $t0, 32($sp)
		sw $t0, -12($sp)
		lw $t0, 24($sp)
		move $a0, $t0
		lw $t0, 20($sp)
		move $a1, $t0
		lw $t0, 16($sp)
		move $a2, $t0
		lw $t0, 12($sp)
		move $a3, $t0
		jal bar
		move $t0, $v0
		sw $t0, 8($sp)
		li $v0, 1
		lw $t0, 24($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 20($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 16($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 12($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 28($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 32($sp)
		move $a0, $t0
		syscall
		li $v0, 4240
		j foo_teardown
foo_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

bar:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -32
		move $t0, $a0
		sw $t0, 20($sp)
		move $t0, $a1
		sw $t0, 16($sp)
		move $t0, $a2
		sw $t0, 12($sp)
		move $t0, $a3
		sw $t0, 8($sp)
		li $t0, -2
		sw $t0, 4($sp)
		lw $t0, 20($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 20($sp)
		li $t0, -2
		sw $t0, 4($sp)
		lw $t0, 16($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		li $t0, -2
		sw $t0, 4($sp)
		lw $t0, 12($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 12($sp)
		li $t0, 2
		sw $t0, 4($sp)
		lw $t0, 8($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 8($sp)
		li $t0, 2
		sw $t0, 4($sp)
		lw $t0, 24($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 24($sp)
		li $t0, 2
		sw $t0, 4($sp)
		lw $t0, 28($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 28($sp)
		li $v0, 1
		lw $t0, 20($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 16($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 12($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 8($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 24($sp)
		move $a0, $t0
		syscall
		li $v0, 1
		lw $t0, 28($sp)
		move $a0, $t0
		syscall
		li $v0, 4240
		j bar_teardown
bar_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

