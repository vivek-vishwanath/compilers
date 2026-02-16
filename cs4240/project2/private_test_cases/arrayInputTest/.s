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
		addi $sp, $fp, -16
		li $a0, 10
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t0, $v0
		sw $t0, 12($sp)
		li $t0, 0
		sw $t0, 8($sp)
main_loop_start:
		li $t0, 9
		sw $t0, 4($sp)
		lw $t0, 8($sp)
		lw $t1, 4($sp)
		bgt $t0, $t1, main_end
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 0($sp)
		li $t0, 4
		sw $t0, 4($sp)
		lw $t0, 8($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 4($sp)
		lw $t0, 12($sp)
		lw $t1, 4($sp)
		add $t0, $t0, $t1
		sw $t0, 4($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, ($t1)
		lw $t0, 8($sp)
		addi $t0, $t0, 1
		sw $t0, 8($sp)
		j main_loop_start
main_end:
		lw $t0, 12($sp)
		move $a0, $t0
		jal sum
		move $t0, $v0
		sw $t0, -4($sp)
		li $v0, 1
		lw $t0, -4($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

sum:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -16
		move $t0, $a0
		sw $t0, 12($sp)
		li $t0, 0
		sw $t0, 8($sp)
		li $t0, 0
		sw $t0, 4($sp)
sum_loop_start:
		li $t0, 9
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bgt $t0, $t1, sum_end
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 12($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t0, ($t0)
		sw $t0, -4($sp)
		lw $t0, 8($sp)
		lw $t1, -4($sp)
		add $t0, $t0, $t1
		sw $t0, 8($sp)
		lw $t0, 4($sp)
		addi $t0, $t0, 1
		sw $t0, 4($sp)
		j sum_loop_start
sum_end:
		lw $t0, 8($sp)
		move $v0, $t0
		j sum_teardown
sum_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

