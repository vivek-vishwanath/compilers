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
		li $v0, 9
		syscall
		move $t7, $v0
		sw $t7, 12($sp)
		li $t7, 0
		sw $t7, 8($sp)
main_loop_start:
		li $t3, 9
		lw $t7, 8($sp)
		bgt $t7, $t3, main_end
		li $v0, 5
		syscall
		move $t7, $v0
		sw $t7, 4($sp)
		li $t3, 4
		lw $t7, 8($sp)
		mul $t4, $t7, $t3
		lw $t3, 12($sp)
		add $t3, $t3, $t4
		lw $t4, 4($sp)
		sw $t4, ($t3)
		addi $t7, $t7, 1
		sw $t7, 8($sp)
		j main_loop_start
main_end:
		lw $t7, 12($sp)
		move $a0, $t7
		jal sum
		move $t7, $v0
		sw $t7, 0($sp)
		li $v0, 1
		lw $t7, 0($sp)
		move $a0, $t7
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
		move $t7, $a0
		sw $t7, 12($sp)
		li $t7, 0
		sw $t7, 4($sp)
		li $t7, 0
		sw $t7, 8($sp)
sum_loop_start:
		li $t7, 9
		lw $t3, 8($sp)
		bgt $t3, $t7, sum_end
		li $t3, 4
		lw $t7, 8($sp)
		mul $t4, $t7, $t3
		lw $t3, 12($sp)
		add $t3, $t3, $t4
		lw $t4, ($t3)
		sw $t4, 0($sp)
		lw $t3, 4($sp)
		add $t3, $t3, $t4
		sw $t3, 4($sp)
		addi $t7, $t7, 1
		sw $t7, 8($sp)
		j sum_loop_start
sum_end:
		lw $t7, 4($sp)
		move $v0, $t7
		j sum_teardown
sum_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

