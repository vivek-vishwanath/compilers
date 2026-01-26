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
		addi $sp, $fp, -16
		li $a0, 10
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t0, $v0
		li $t3, 0
main_loop_start:
		li $t1, 9
		bgt $t3, $t1, main_end
		li $v0, 5
		syscall
		move $t2, $v0
		li $t1, 4
		mul $t4, $t3, $t1
		add $t1, $t0, $t4
		sw $t2, ($t1)
		addi $t3, $t3, 1
		j main_loop_start
main_end:
		move $a0, $t0
		addi $sp, $sp, -4
		sw $t0, 0($sp)
		jal sum
		lw $t0, 0($sp)
		addi $sp, $sp, 4
		move $t0, $v0
		li $v0, 1
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
		li $t4, 0
		li $t2, 0
sum_loop_start:
		li $t1, 9
		bgt $t2, $t1, sum_end
		li $t1, 4
		mul $t3, $t2, $t1
		add $t1, $t0, $t3
		lw $t3, ($t1)
		add $t4, $t4, $t3
		addi $t2, $t2, 1
		j sum_loop_start
sum_end:
		move $v0, $t4
		j sum_teardown
sum_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

