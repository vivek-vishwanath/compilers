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
		move $t1, $v0
		li $t3, 0
		li $t0, 4240
		sw $t0, 0($t1)
		li $t0, 4290
		sw $t0, 4($t1)
		li $t0, 3220
		sw $t0, 8($t1)
main_print_loop:
		li $t0, 3
		bge $t3, $t0, main_end
		li $t2, 4
		mul $t0, $t3, $t2
		add $t2, $t1, $t0
		lw $t0, ($t2)
		li $v0, 1
		move $a0, $t0
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		addi $t3, $t3, 1
		j main_print_loop
main_end:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

