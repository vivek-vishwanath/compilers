.data
	STACK: .word -2147483648

.text
	lw $sp, STACK
	move $fp, $sp
	jal main
	li $v0, 10
	syscall

quicksort:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -44
		move $t2, $a0
		sw $t2, 32($sp)
		move $t2, $a1
		sw $t2, 36($sp)
		move $t2, $a2
		sw $t2, 40($sp)
		li $t2, 0
		sw $t2, 24($sp)
		li $t2, 0
		sw $t2, 28($sp)
		lw $t4, 36($sp)
		lw $t2, 40($sp)
		bge $t4, $t2, quicksort_end
		lw $t4, 36($sp)
		lw $t2, 40($sp)
		add $t3, $t4, $t2
		sw $t3, 16($sp)
		li $t1, 2
		div $t3, $t3, $t1
		sw $t3, 16($sp)
		li $t1, 4
		mul $t1, $t3, $t1
		lw $t3, 32($sp)
		add $t1, $t3, $t1
		lw $t1, ($t1)
		sw $t1, 20($sp)
		li $t1, 1
		sub $t4, $t4, $t1
		sw $t4, 24($sp)
		addi $t2, $t2, 1
		sw $t2, 28($sp)
quicksort_loop0:
		add $zero, $zero, $zero
quicksort_loop1:
		lw $t2, 24($sp)
		addi $t4, $t2, 1
		sw $t4, 24($sp)
		li $t2, 4
		mul $t2, $t4, $t2
		lw $t4, 32($sp)
		add $t2, $t4, $t2
		lw $t2, ($t2)
		sw $t2, 8($sp)
		move $t2, $t2
		sw $t2, 12($sp)
		lw $t4, 20($sp)
		blt $t2, $t4, quicksort_loop1
quicksort_loop2:
		li $t2, 1
		lw $t4, 28($sp)
		sub $t4, $t4, $t2
		sw $t4, 28($sp)
		li $t2, 4
		mul $t2, $t4, $t2
		lw $t4, 32($sp)
		add $t2, $t4, $t2
		lw $t2, ($t2)
		sw $t2, 8($sp)
		move $t4, $t2
		sw $t4, 4($sp)
		lw $t2, 20($sp)
		bgt $t4, $t2, quicksort_loop2
		lw $t4, 28($sp)
		lw $t2, 24($sp)
		bge $t2, $t4, quicksort_exit0
		li $t4, 4
		lw $t2, 28($sp)
		mul $t4, $t2, $t4
		lw $t2, 32($sp)
		add $t4, $t2, $t4
		lw $t1, 12($sp)
		sw $t1, ($t4)
		li $t4, 4
		lw $t1, 24($sp)
		mul $t4, $t1, $t4
		add $t2, $t2, $t4
		lw $t4, 4($sp)
		sw $t4, ($t2)
		j quicksort_loop0
quicksort_exit0:
		lw $t2, 28($sp)
		addi $t4, $t2, 1
		sw $t4, 0($sp)
		lw $t4, 32($sp)
		move $a0, $t4
		lw $t4, 36($sp)
		move $a1, $t4
		move $a2, $t2
		jal quicksort
		lw $t2, 28($sp)
		addi $t2, $t2, 1
		sw $t2, 28($sp)
		lw $t4, 32($sp)
		move $a0, $t4
		move $a1, $t2
		lw $t2, 40($sp)
		move $a2, $t2
		jal quicksort
quicksort_end:
		add $zero, $zero, $zero
quicksort_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

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
		move $t2, $v0
		sw $t2, 12($sp)
		li $t2, 0
		sw $t2, 4($sp)
		li $v0, 5
		syscall
		move $t2, $v0
		sw $t2, 8($sp)
		li $t2, 100
		lw $t4, 8($sp)
		bgt $t4, $t2, main_return
		li $t2, 1
		lw $t4, 8($sp)
		sub $t2, $t4, $t2
		sw $t2, 8($sp)
		li $t2, 0
		sw $t2, 0($sp)
main_loop0:
		lw $t4, 0($sp)
		lw $t2, 8($sp)
		bgt $t4, $t2, main_exit0
		li $v0, 5
		syscall
		move $t2, $v0
		sw $t2, 4($sp)
		li $t4, 4
		lw $t2, 0($sp)
		mul $t1, $t2, $t4
		lw $t4, 12($sp)
		add $t4, $t4, $t1
		lw $t1, 4($sp)
		sw $t1, ($t4)
		addi $t2, $t2, 1
		sw $t2, 0($sp)
		j main_loop0
main_exit0:
		lw $t2, 12($sp)
		move $a0, $t2
		li $a1, 0
		lw $t2, 8($sp)
		move $a2, $t2
		jal quicksort
		li $t2, 0
		sw $t2, 0($sp)
main_loop1:
		lw $t4, 8($sp)
		lw $t2, 0($sp)
		bgt $t2, $t4, main_exit1
		li $t4, 4
		lw $t2, 0($sp)
		mul $t2, $t2, $t4
		lw $t4, 12($sp)
		add $t2, $t4, $t2
		lw $t2, ($t2)
		sw $t2, 4($sp)
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		lw $t2, 0($sp)
		addi $t2, $t2, 1
		sw $t2, 0($sp)
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

