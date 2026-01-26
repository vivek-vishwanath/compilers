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
		addi $sp, $fp, -52
		li $t0, 0
		sw $t0, 48($sp)
		li $t0, 1
		sw $t0, 44($sp)
		li $t0, 2
		sw $t0, 40($sp)
		li $t0, 3
		sw $t0, 36($sp)
		lw $t0, 36($sp)
		lw $t1, 36($sp)
		add $t0, $t0, $t1
		sw $t0, 36($sp)
		li $t0, 4
		sw $t0, 32($sp)
		li $t0, 5
		sw $t0, 28($sp)
		li $t0, 6
		sw $t0, 24($sp)
		li $t0, 7
		sw $t0, 20($sp)
		li $t0, 8
		sw $t0, 16($sp)
		li $t0, 9
		sw $t0, 12($sp)
		li $t0, 10
		sw $t0, 8($sp)
		li $t0, 11
		sw $t0, 4($sp)
		li $t0, 12
		sw $t0, 0($sp)
		lw $t0, 32($sp)
		lw $t1, 32($sp)
		add $t0, $t0, $t1
		sw $t0, 32($sp)
		lw $t0, 28($sp)
		lw $t1, 28($sp)
		add $t0, $t0, $t1
		sw $t0, 28($sp)
		lw $t0, 24($sp)
		lw $t1, 24($sp)
		add $t0, $t0, $t1
		sw $t0, 24($sp)
		lw $t0, 20($sp)
		lw $t1, 20($sp)
		add $t0, $t0, $t1
		sw $t0, 20($sp)
		lw $t0, 16($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 12($sp)
		lw $t1, 12($sp)
		add $t0, $t0, $t1
		sw $t0, 12($sp)
		lw $t0, 8($sp)
		lw $t1, 8($sp)
		add $t0, $t0, $t1
		sw $t0, 8($sp)
		lw $t0, 4($sp)
		lw $t1, 4($sp)
		add $t0, $t0, $t1
		sw $t0, 4($sp)
		lw $t0, 0($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 44($sp)
		lw $t1, 40($sp)
		add $t0, $t0, $t1
		sw $t0, 48($sp)
		li $v0, 1
		lw $t0, 48($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 44($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 40($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 36($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 32($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 28($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 24($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 20($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 16($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 12($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 8($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 4($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		li $v0, 1
		lw $t0, 0($sp)
		move $a0, $t0
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

