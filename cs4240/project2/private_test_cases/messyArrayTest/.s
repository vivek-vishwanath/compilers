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
		addi $sp, $fp, -12
		li $a0, 3
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t0, $v0
		sw $t0, 8($sp)
		li $t0, 0
		sw $t0, 4($sp)
		li $t0, -1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 8($sp)
		sw $t0, 0($t1)
main_init_loop:
		li $t0, 2
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bge $t0, $t1, main_after_first_loop
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 8($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		li $t0, 4240
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 0($sp)
		sw $t0, ($t1)
		lw $t0, 4($sp)
		addi $t0, $t0, 1
		sw $t0, 4($sp)
		j main_init_loop
main_after_first_loop:
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 8($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		li $t0, 1331
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 0($sp)
		sw $t0, ($t1)
		li $t0, 0
		sw $t0, 4($sp)
main_print_loop:
		li $t0, 3
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bge $t0, $t1, main_end
		li $t0, 4
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 8($sp)
		lw $t1, 0($sp)
		add $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t0, ($t0)
		sw $t0, -4($sp)
		li $v0, 1
		lw $t0, -4($sp)
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		lw $t0, 4($sp)
		addi $t0, $t0, 1
		sw $t0, 4($sp)
		j main_print_loop
main_end:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

