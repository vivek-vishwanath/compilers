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
		move $t0, $a0
		sw $t0, 40($sp)
		move $t0, $a1
		sw $t0, 36($sp)
		move $t0, $a2
		sw $t0, 32($sp)
		li $t0, 0
		sw $t0, 28($sp)
		li $t0, 0
		sw $t0, 24($sp)
		lw $t0, 36($sp)
		lw $t1, 32($sp)
		bge $t0, $t1, quicksort_end
		lw $t0, 36($sp)
		lw $t1, 32($sp)
		add $t0, $t0, $t1
		sw $t0, 20($sp)
		li $t0, 2
		sw $t0, 16($sp)
		lw $t0, 20($sp)
		lw $t1, 16($sp)
		div $t0, $t0, $t1
		sw $t0, 20($sp)
		li $t0, 4
		sw $t0, 16($sp)
		lw $t0, 20($sp)
		lw $t1, 16($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 40($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 16($sp)
		lw $t0, ($t0)
		sw $t0, 12($sp)
		li $t0, 1
		sw $t0, 16($sp)
		lw $t0, 36($sp)
		lw $t1, 16($sp)
		sub $t0, $t0, $t1
		sw $t0, 28($sp)
		lw $t0, 32($sp)
		addi $t0, $t0, 1
		sw $t0, 24($sp)
quicksort_loop0:
		add $zero, $zero, $zero
quicksort_loop1:
		lw $t0, 28($sp)
		addi $t0, $t0, 1
		sw $t0, 28($sp)
		li $t0, 4
		sw $t0, 16($sp)
		lw $t0, 28($sp)
		lw $t1, 16($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 40($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 16($sp)
		lw $t0, ($t0)
		sw $t0, 8($sp)
		lw $t0, 8($sp)
		move $t0, $t0
		sw $t0, 4($sp)
		lw $t0, 4($sp)
		lw $t1, 12($sp)
		blt $t0, $t1, quicksort_loop1
quicksort_loop2:
		li $t0, 1
		sw $t0, 16($sp)
		lw $t0, 24($sp)
		lw $t1, 16($sp)
		sub $t0, $t0, $t1
		sw $t0, 24($sp)
		li $t0, 4
		sw $t0, 16($sp)
		lw $t0, 24($sp)
		lw $t1, 16($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 40($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 16($sp)
		lw $t0, ($t0)
		sw $t0, 8($sp)
		lw $t0, 8($sp)
		move $t0, $t0
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 12($sp)
		bgt $t0, $t1, quicksort_loop2
		lw $t0, 28($sp)
		lw $t1, 24($sp)
		bge $t0, $t1, quicksort_exit0
		li $t0, 4
		sw $t0, 16($sp)
		lw $t0, 24($sp)
		lw $t1, 16($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 40($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 4($sp)
		lw $t1, 16($sp)
		sw $t0, ($t1)
		li $t0, 4
		sw $t0, 16($sp)
		lw $t0, 28($sp)
		lw $t1, 16($sp)
		mul $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 40($sp)
		lw $t1, 16($sp)
		add $t0, $t0, $t1
		sw $t0, 16($sp)
		lw $t0, 0($sp)
		lw $t1, 16($sp)
		sw $t0, ($t1)
		j quicksort_loop0
quicksort_exit0:
		lw $t0, 24($sp)
		addi $t0, $t0, 1
		sw $t0, -4($sp)
		lw $t0, 40($sp)
		move $a0, $t0
		lw $t0, 36($sp)
		move $a1, $t0
		lw $t0, 24($sp)
		move $a2, $t0
		jal quicksort
		lw $t0, 24($sp)
		addi $t0, $t0, 1
		sw $t0, 24($sp)
		lw $t0, 40($sp)
		move $a0, $t0
		lw $t0, 24($sp)
		move $a1, $t0
		lw $t0, 32($sp)
		move $a2, $t0
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
		move $t0, $v0
		sw $t0, 12($sp)
		li $t0, 0
		sw $t0, 8($sp)
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 4($sp)
		li $t0, 100
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bgt $t0, $t1, main_return
		li $t0, 1
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		sub $t0, $t0, $t1
		sw $t0, 4($sp)
		li $t0, 0
		sw $t0, -4($sp)
main_loop0:
		lw $t0, -4($sp)
		lw $t1, 4($sp)
		bgt $t0, $t1, main_exit0
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 8($sp)
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, -4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 12($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 8($sp)
		lw $t1, 0($sp)
		sw $t0, ($t1)
		lw $t0, -4($sp)
		addi $t0, $t0, 1
		sw $t0, -4($sp)
		j main_loop0
main_exit0:
		lw $t0, 12($sp)
		move $a0, $t0
		li $a1, 0
		lw $t0, 4($sp)
		move $a2, $t0
		jal quicksort
		li $t0, 0
		sw $t0, -4($sp)
main_loop1:
		lw $t0, -4($sp)
		lw $t1, 4($sp)
		bgt $t0, $t1, main_exit1
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, -4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 12($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t0, ($t0)
		sw $t0, 8($sp)
		li $v0, 1
		lw $t0, 8($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		lw $t0, -4($sp)
		addi $t0, $t0, 1
		sw $t0, -4($sp)
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

