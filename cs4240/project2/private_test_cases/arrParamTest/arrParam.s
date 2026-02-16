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
		addi $sp, $fp, -8
		li $a0, 3
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t1, $v0
		li $t0, 0
		sw $t0, 0($t1)
		li $t0, 0
		sw $t0, 4($t1)
		li $t0, 0
		sw $t0, 8($t1)
		li $t0, -1
		sw $t0, 0($t1)
		li $t0, 1
		sw $t0, 8($t1)
		move $a0, $t1
		addi $sp, $sp, -0
		jal arrPrinter
		addi $sp, $sp, 0
		move $t0, $v0
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

arrPrinter:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -12
		move $t3, $a0
		li $t1, 0
arrPrinter_loop_start:
		li $t0, 3
		bge $t1, $t0, arrPrinter_loop_exit
		li $t0, 4
		mul $t2, $t1, $t0
		add $t0, $t3, $t2
		lw $t2, ($t0)
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		addi $t1, $t1, 1
		j arrPrinter_loop_start
arrPrinter_loop_exit:
		li $v0, 123
		j arrPrinter_teardown
arrPrinter_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

