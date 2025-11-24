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
		addi $sp, $fp, -8
		li $a0, 3
		add $a0, $a0, $a0
		add $a0, $a0, $a0
		li $v0, 9
		syscall
		move $t0, $v0
		sw $t0, 4($sp)
		li $t0, 0
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, 0($t1)
		li $t0, 0
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, 4($t1)
		li $t0, 0
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, 8($t1)
		li $t0, -1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, 0($t1)
		li $t0, 1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		sw $t0, 8($t1)
		lw $t0, 4($sp)
		move $a0, $t0
		jal arrPrinter
		move $t0, $v0
		sw $t0, -4($sp)
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
		move $t0, $a0
		sw $t0, 8($sp)
		li $t0, 0
		sw $t0, 4($sp)
arrPrinter_loop_start:
		li $t0, 3
		sw $t0, 0($sp)
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bge $t0, $t1, arrPrinter_loop_exit
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
		j arrPrinter_loop_start
arrPrinter_loop_exit:
		li $v0, 123
		j arrPrinter_teardown
arrPrinter_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

