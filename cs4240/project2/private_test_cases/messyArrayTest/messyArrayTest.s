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
		addi $sp, $fp, -12
		li $a0, 3
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t2, $v0
		li $t0, 0
		li $t1, -1
		sw $t1, 0($t2)
main_init_loop:
		li $t1, 2
		bge $t0, $t1, main_after_first_loop
		li $t1, 4
		mul $t3, $t0, $t1
		add $t1, $t2, $t3
		li $t3, 4240
		sw $t3, ($t1)
		addi $t0, $t0, 1
		j main_init_loop
main_after_first_loop:
		li $t1, 4
		mul $t3, $t0, $t1
		add $t1, $t2, $t3
		li $t0, 1331
		sw $t0, ($t1)
		li $t0, 0
main_print_loop:
		li $t1, 3
		bge $t0, $t1, main_end
		li $t3, 4
		mul $t1, $t0, $t3
		add $t3, $t2, $t1
		lw $t1, ($t3)
		li $v0, 1
		move $a0, $t1
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		addi $t0, $t0, 1
		j main_print_loop
main_end:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

