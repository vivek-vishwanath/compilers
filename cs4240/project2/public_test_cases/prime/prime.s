.data
	STACK: .word -2147483648

.text
	lw $sp, STACK
	move $fp, $sp
	jal main
	li $v0, 10
	syscall

divisible:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -12
		move $t7, $a0
		sw $t7, 4($sp)
		move $t7, $a1
		sw $t7, 8($sp)
		lw $t2, 8($sp)
		lw $t7, 4($sp)
		div $t3, $t7, $t2
		sw $t3, 0($sp)
		mul $t2, $t3, $t2
		sw $t2, 0($sp)
		bne $t7, $t2, divisible_label0
		li $v0, 1
		j divisible_teardown
divisible_label0:
		li $v0, 0
		j divisible_teardown
divisible_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

main:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -56
		li $t7, 0
		sw $t7, 32($sp)
		li $t7, 2
		sw $t7, 36($sp)
		li $t7, 3
		sw $t7, 40($sp)
		li $t7, 6
		sw $t7, 44($sp)
		li $t7, 0
		sw $t7, 48($sp)
		li $v0, 5
		syscall
		move $t7, $v0
		sw $t7, 52($sp)
		li $t2, 1
		lw $t7, 52($sp)
		bgt $t7, $t2, main_label0
		li $t7, 0
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		j main_print
main_label0:
		li $t7, 3
		lw $t2, 52($sp)
		bgt $t2, $t7, main_label1
		li $t7, 1
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		j main_print
main_label1:
		lw $t7, 52($sp)
		move $a0, $t7
		lw $t7, 36($sp)
		move $a1, $t7
		jal divisible
		move $t7, $v0
		sw $t7, 20($sp)
		lw $t7, 48($sp)
		move $t7, $t7
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		li $t7, 1
		lw $t2, 20($sp)
		beq $t2, $t7, main_label2
		lw $t7, 52($sp)
		move $a0, $t7
		lw $t7, 40($sp)
		move $a1, $t7
		jal divisible
		move $t7, $v0
		sw $t7, 20($sp)
		lw $t7, 48($sp)
		move $t7, $t7
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		li $t2, 1
		lw $t7, 20($sp)
		beq $t7, $t2, main_label2
		j main_label3
main_label2:
		j main_print
main_label3:
		li $t7, 5
		sw $t7, 32($sp)
main_loop:
		lw $t7, 32($sp)
		mul $t2, $t7, $t7
		sw $t2, 16($sp)
		lw $t7, 52($sp)
		bgt $t2, $t7, main_exit
		lw $t7, 52($sp)
		move $a0, $t7
		lw $t7, 32($sp)
		move $a1, $t7
		jal divisible
		move $t7, $v0
		sw $t7, 20($sp)
		lw $t7, 48($sp)
		move $t7, $t7
		sw $t7, 24($sp)
		li $t2, 0
		sw $t2, 8($sp)
		li $t2, 0
		sw $t2, 12($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		li $t7, 1
		lw $t2, 20($sp)
		beq $t2, $t7, main_label2
		lw $t7, 32($sp)
		addi $t7, $t7, 2
		sw $t7, 4($sp)
		lw $t2, 52($sp)
		move $a0, $t2
		move $a1, $t7
		jal divisible
		move $t7, $v0
		sw $t7, 20($sp)
		lw $t7, 48($sp)
		move $t7, $t7
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
		li $t2, 1
		lw $t7, 20($sp)
		beq $t7, $t2, main_label2
		lw $t7, 32($sp)
		addi $t7, $t7, 6
		sw $t7, 32($sp)
		j main_loop
main_exit:
		lw $t7, 8($sp)
		move $t7, $t7
		sw $t7, 0($sp)
		lw $t7, 12($sp)
		move $t7, $t7
		sw $t7, 24($sp)
		li $t7, 1
		sw $t7, 24($sp)
		move $t7, $t7
		sw $t7, 28($sp)
main_print:
		li $v0, 1
		lw $t7, 28($sp)
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

