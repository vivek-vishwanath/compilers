.data
	STACK: .word -2147483648

.text
	lw $sp, STACK
	move $fp, $sp
	jal main
	li $v0, 10
	syscall

operate:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -20
		add $t0, $a0, $a1
		sw $t0, 4($sp)
		add $t0, $t0, $a2
		sw $t0, 12($sp)
		add $t0, $t0, $a3
		sw $t0, 4($sp)
		add $t2, $t0, $a0
		sw $t2, 4($sp)
		lw $t0, 12($sp)
		add $t0, $a0, $t0
		sw $t0, 8($sp)
		add $t0, $t2, $a2
		sw $t0, 12($sp)
operate_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

divisible:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -16
		li $a0, 2
		sw $a0, 8($sp)
		li $a0, 3
		sw $a0, 12($sp)
		move $a0, $a1
		li $a1, x
		li $a2, y
		li $a3, 1
		jal x
		div $t0, $a0, $a1
		sw $t0, 12($sp)
		mul $t0, $t0, $a1
		sw $t0, 12($sp)
		bne $a0, $t0, divisible_label0
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
		addi $sp, $fp, -68
		li $t0, 0
		sw $t0, 44($sp)
		li $t0, 2
		sw $t0, 48($sp)
		li $t0, 3
		sw $t0, 52($sp)
		li $t0, 6
		sw $t0, 56($sp)
		li $t0, 0
		sw $t0, 60($sp)
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 64($sp)
		li $t2, 1
		sw $t2, 40($sp)
		lw $t0, 64($sp)
		bgt $t0, $t2, main_label0
		li $t0, 0
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		j main_print
main_label0:
		li $t2, 3
		sw $t2, 40($sp)
		lw $t0, 64($sp)
		bgt $t0, $t2, main_label1
		li $t0, 1
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		j main_print
main_label1:
		lw $t0, 64($sp)
		move $a0, $t0
		lw $t0, 48($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 28($sp)
		lw $t0, 60($sp)
		move $t0, $t0
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		li $t0, 1
		lw $t2, 28($sp)
		sw $t0, 40($sp)
		beq $t2, $t0, main_label2
		lw $t0, 64($sp)
		move $a0, $t0
		lw $t0, 52($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 28($sp)
		lw $t0, 60($sp)
		move $t0, $t0
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		li $t0, 1
		lw $t2, 28($sp)
		sw $t0, 40($sp)
		beq $t2, $t0, main_label2
		j main_label3
main_label2:
		j main_print
main_label3:
		li $t0, 5
		sw $t0, 44($sp)
main_loop:
		lw $t0, 44($sp)
		mul $t2, $t0, $t0
		sw $t2, 24($sp)
		lw $t0, 64($sp)
		bgt $t2, $t0, main_exit
		lw $t0, 64($sp)
		move $a0, $t0
		lw $t0, 44($sp)
		move $a1, $t0
		jal divisible
		move $t0, $v0
		sw $t0, 28($sp)
		lw $t0, 60($sp)
		move $t0, $t0
		sw $t0, 32($sp)
		li $t0, 0
		sw $t0, 16($sp)
		li $t0, 0
		sw $t0, 20($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		li $t0, 1
		lw $t2, 28($sp)
		sw $t0, 40($sp)
		beq $t2, $t0, main_label2
		lw $t0, 44($sp)
		addi $t2, $t0, 2
		sw $t2, 12($sp)
		lw $t0, 64($sp)
		move $a0, $t0
		move $a1, $t2
		jal divisible
		move $t0, $v0
		sw $t0, 28($sp)
		lw $t0, 60($sp)
		move $t0, $t0
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
		li $t2, 1
		sw $t2, 40($sp)
		lw $t0, 28($sp)
		beq $t0, $t2, main_label2
		lw $t0, 44($sp)
		addi $t0, $t0, 6
		sw $t0, 44($sp)
		j main_loop
main_exit:
		lw $t0, 16($sp)
		move $t0, $t0
		sw $t0, 8($sp)
		lw $t0, 20($sp)
		move $t0, $t0
		sw $t0, 32($sp)
		li $t0, 1
		sw $t0, 32($sp)
		move $t0, $t0
		sw $t0, 36($sp)
main_print:
		li $v0, 1
		lw $t0, 36($sp)
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

