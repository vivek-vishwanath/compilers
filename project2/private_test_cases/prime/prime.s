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
		move $t0, $a0
		sw $t0, 8($sp)
		move $t0, $a1
		sw $t0, 4($sp)
		lw $t0, 8($sp)
		lw $t1, 4($sp)
		div $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 0($sp)
		lw $t1, 4($sp)
		mul $t0, $t0, $t1
		sw $t0, 0($sp)
		lw $t0, 8($sp)
		lw $t1, 0($sp)
		bne $t0, $t1, divisible_label0
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
		li $t0, 0
		sw $t0, 52($sp)
		li $t0, 2
		sw $t0, 48($sp)
		li $t0, 3
		sw $t0, 44($sp)
		li $t0, 6
		sw $t0, 40($sp)
		li $t0, 0
		sw $t0, 36($sp)
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 32($sp)
		li $t0, 1
		sw $t0, 28($sp)
		lw $t0, 32($sp)
		lw $t1, 28($sp)
		bgt $t0, $t1, main_label0
		li $t0, 0
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		j main_print
main_label0:
		li $t0, 3
		sw $t0, 28($sp)
		lw $t0, 32($sp)
		lw $t1, 28($sp)
		bgt $t0, $t1, main_label1
		li $t0, 1
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		j main_print
main_label1:
		lw $t0, 32($sp)
		move $a0, $t0
		lw $t0, 48($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 16($sp)
		lw $t0, 36($sp)
		move $t0, $t0
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		li $t0, 1
		sw $t0, 28($sp)
		lw $t0, 16($sp)
		lw $t1, 28($sp)
		beq $t0, $t1, main_label2
		lw $t0, 32($sp)
		move $a0, $t0
		lw $t0, 44($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 16($sp)
		lw $t0, 36($sp)
		move $t0, $t0
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		li $t0, 1
		sw $t0, 28($sp)
		lw $t0, 16($sp)
		lw $t1, 28($sp)
		beq $t0, $t1, main_label2
		j main_label3
main_label2:
		j main_print
main_label3:
		li $t0, 5
		sw $t0, 52($sp)
main_loop:
		lw $t0, 52($sp)
		lw $t1, 52($sp)
		mul $t0, $t0, $t1
		sw $t0, 12($sp)
		lw $t0, 12($sp)
		lw $t1, 32($sp)
		bgt $t0, $t1, main_exit
		lw $t0, 32($sp)
		move $a0, $t0
		lw $t0, 52($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 16($sp)
		lw $t0, 36($sp)
		move $t0, $t0
		sw $t0, 24($sp)
		li $t0, 0
		sw $t0, 8($sp)
		li $t0, 0
		sw $t0, 4($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		li $t0, 1
		sw $t0, 28($sp)
		lw $t0, 16($sp)
		lw $t1, 28($sp)
		beq $t0, $t1, main_label2
		lw $t0, 52($sp)
		addi $t0, $t0, 2
		sw $t0, 0($sp)
		lw $t0, 32($sp)
		move $a0, $t0
		lw $t0, 0($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 16($sp)
		lw $t0, 36($sp)
		move $t0, $t0
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
		li $t0, 1
		sw $t0, 28($sp)
		lw $t0, 16($sp)
		lw $t1, 28($sp)
		beq $t0, $t1, main_label2
		lw $t0, 52($sp)
		addi $t0, $t0, 6
		sw $t0, 52($sp)
		j main_loop
main_exit:
		lw $t0, 8($sp)
		move $t0, $t0
		sw $t0, -4($sp)
		lw $t0, 4($sp)
		move $t0, $t0
		sw $t0, 24($sp)
		li $t0, 1
		sw $t0, 24($sp)
		lw $t0, 24($sp)
		move $t0, $t0
		sw $t0, 20($sp)
main_print:
		li $v0, 1
		lw $t0, 20($sp)
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

