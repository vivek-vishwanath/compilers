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
		li $a0, 100
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t1, $v0
		li $t5, 0
		li $v0, 5
		syscall
		move $t4, $v0
		li $t0, 100
		bgt $t4, $t0, main_return
		li $t0, 1
		sub $t4, $t4, $t0
		li $t3, 0
main_loop0:
		bgt $t3, $t4, main_exit0
		li $v0, 5
		syscall
		move $t5, $v0
		li $t2, 4
		mul $t0, $t3, $t2
		add $t2, $t1, $t0
		sw $t5, ($t2)
		addi $t3, $t3, 1
		j main_loop0
main_exit0:
		move $a0, $t1
		li $a1, 0
		move $a2, $t4
		addi $sp, $sp, -8
		sw $t4, 4($sp)
		sw $t1, 0($sp)
		jal quicksort
		lw $t4, 4($sp)
		lw $t1, 0($sp)
		addi $sp, $sp, 8
		li $t3, 0
main_loop1:
		bgt $t3, $t4, main_exit1
		li $t0, 4
		mul $t2, $t3, $t0
		add $t0, $t1, $t2
		lw $t5, ($t0)
		li $v0, 1
		move $a0, $t5
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		addi $t3, $t3, 1
		j main_loop1
main_exit1:
		add $zero, $zero, $zero
main_return:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

quicksort:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -44
		move $18, $a0
		move $t7, $a1
		move $t6, $a2
		li $t5, 0
		li $t4, 0
		bge $t7, $t6, quicksort_end
		add $t0, $t7, $t6
		li $t1, 2
		div $t0, $t0, $t1
		li $t2, 4
		mul $t1, $t0, $t2
		add $t0, $18, $t1
		lw $t3, ($t0)
		li $t0, 1
		sub $t5, $t7, $t0
		addi $t4, $t6, 1
quicksort_loop0:
		add $zero, $zero, $zero
quicksort_loop1:
		addi $t5, $t5, 1
		li $t0, 4
		mul $t1, $t5, $t0
		add $t0, $18, $t1
		lw $t1, ($t0)
		move $t2, $t1
		blt $t2, $t3, quicksort_loop1
quicksort_loop2:
		li $t0, 1
		sub $t4, $t4, $t0
		li $t0, 4
		mul $t1, $t4, $t0
		add $t0, $18, $t1
		lw $t1, ($t0)
		move $t0, $t1
		bgt $t0, $t3, quicksort_loop2
		bge $t5, $t4, quicksort_exit0
		li $t1, 4
		mul $t3, $t4, $t1
		add $t1, $18, $t3
		sw $t2, ($t1)
		li $t2, 4
		mul $t1, $t5, $t2
		add $t2, $18, $t1
		sw $t0, ($t2)
		j quicksort_loop0
quicksort_exit0:
		addi $t0, $t4, 1
		move $a0, $18
		move $a1, $t7
		move $a2, $t4
		addi $sp, $sp, -8
		sw $t4, 4($sp)
		sw $t6, 0($sp)
		jal quicksort
		lw $t4, 4($sp)
		lw $t6, 0($sp)
		addi $sp, $sp, 8
		addi $t4, $t4, 1
		move $a0, $18
		move $a1, $t4
		move $a2, $t6
		addi $sp, $sp, -0
		jal quicksort
		addi $sp, $sp, 0
quicksort_end:
		add $zero, $zero, $zero
quicksort_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

