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
		addi $sp, $fp, -28
		li $t3, 123
		li $t1, 456
		li $t4, 789
		li $t0, -123
		li $t5, -456
		li $t2, -789
		sw $t5, -16($sp)
		sw $t2, -12($sp)
		move $a0, $t3
		move $a1, $t1
		move $a2, $t4
		move $a3, $t0
		addi $sp, $sp, -0
		jal foo
		addi $sp, $sp, 0
		move $t0, $v0
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
		move $t3, $a0
		move $t1, $a1
		move $t4, $a2
		move $t0, $a3
		sw $t5, -16($sp)
		sw $t2, -12($sp)
		move $a0, $t3
		move $a1, $t1
		move $a2, $t4
		move $a3, $t0
		addi $sp, $sp, -24
		sw $t2, 20($sp)
		sw $t0, 16($sp)
		sw $t3, 12($sp)
		sw $t4, 8($sp)
		sw $t1, 4($sp)
		sw $t5, 0($sp)
		jal bar
		lw $t2, 20($sp)
		lw $t0, 16($sp)
		lw $t3, 12($sp)
		lw $t4, 8($sp)
		lw $t1, 4($sp)
		lw $t5, 0($sp)
		addi $sp, $sp, 24
		move $t0, $v0
		li $v0, 1
		move $a0, $t3
		syscall
		li $v0, 1
		move $a0, $t1
		syscall
		li $v0, 1
		move $a0, $t4
		syscall
		li $v0, 1
		move $a0, $t0
		syscall
		li $v0, 1
		move $a0, $t5
		syscall
		li $v0, 1
		move $a0, $t2
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
		move $t4, $a0
		move $t0, $a1
		move $t5, $a2
		move $t2, $a3
		li $t3, -2
		mul $t4, $t4, $t3
		li $t3, -2
		mul $t0, $t0, $t3
		li $t3, -2
		mul $t5, $t5, $t3
		li $t3, 2
		mul $t2, $t2, $t3
		li $t3, 2
		mul $t6, $t6, $t3
		li $t3, 2
		mul $t1, $t1, $t3
		li $v0, 1
		move $a0, $t4
		syscall
		li $v0, 1
		move $a0, $t0
		syscall
		li $v0, 1
		move $a0, $t5
		syscall
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 1
		move $a0, $t6
		syscall
		li $v0, 1
		move $a0, $t1
		syscall
		li $v0, 4240
		j bar_teardown
bar_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

