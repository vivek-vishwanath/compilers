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

divisible:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -12
		move $t1, $a0
		move $t0, $a1
		div $t2, $t1, $t0
		mul $t2, $t2, $t0
		bne $t1, $t2, divisible_label0
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
		li $t1, 0
		li $t4, 2
		li $t1, 3
		li $t0, 6
		li $t2, 0
		li $v0, 5
		syscall
		move $t5, $v0
		li $t3, 1
		bgt $t5, $t3, main_label0
		li $t4, 0
		move $t3, $t4
		j main_print
main_label0:
		li $t3, 3
		bgt $t5, $t3, main_label1
		li $t4, 1
		move $t3, $t4
		j main_print
main_label1:
		move $a0, $t5
		move $a1, $t4
		addi $sp, $sp, -20
		sw $t2, 16($sp)
		sw $t1, 12($sp)
		sw $t6, 8($sp)
		sw $t5, 4($sp)
		sw $t0, 0($sp)
		jal divisible
		lw $t2, 16($sp)
		lw $t1, 12($sp)
		lw $t6, 8($sp)
		lw $t5, 4($sp)
		lw $t0, 0($sp)
		addi $sp, $sp, 20
		move $t7, $v0
		move $t4, $t2
		move $t3, $t4
		li $t4, 1
		beq $t7, $t4, main_label2
		move $a0, $t5
		move $a1, $t1
		addi $sp, $sp, -16
		sw $t2, 12($sp)
		sw $t6, 8($sp)
		sw $t5, 4($sp)
		sw $t0, 0($sp)
		jal divisible
		lw $t2, 12($sp)
		lw $t6, 8($sp)
		lw $t5, 4($sp)
		lw $t0, 0($sp)
		addi $sp, $sp, 16
		move $t7, $v0
		move $t4, $t2
		move $t3, $t4
		li $t1, 1
		beq $t7, $t1, main_label2
		j main_label3
main_label2:
		j main_print
main_label3:
		li $t1, 5
main_loop:
		mul $t3, $t1, $t1
		bgt $t3, $t5, main_exit
		move $a0, $t5
		move $a1, $t1
		addi $sp, $sp, -12
		sw $t2, 8($sp)
		sw $t1, 4($sp)
		sw $t5, 0($sp)
		jal divisible
		lw $t2, 8($sp)
		lw $t1, 4($sp)
		lw $t5, 0($sp)
		addi $sp, $sp, 12
		move $t7, $v0
		move $t4, $t2
		li $t0, 0
		li $t6, 0
		move $t3, $t4
		li $t4, 1
		beq $t7, $t4, main_label2
		addi $t3, $t1, 2
		move $a0, $t5
		move $a1, $t3
		addi $sp, $sp, -20
		sw $t2, 16($sp)
		sw $t1, 12($sp)
		sw $t6, 8($sp)
		sw $t5, 4($sp)
		sw $t0, 0($sp)
		jal divisible
		lw $t2, 16($sp)
		lw $t1, 12($sp)
		lw $t6, 8($sp)
		lw $t5, 4($sp)
		lw $t0, 0($sp)
		addi $sp, $sp, 20
		move $t7, $v0
		move $t4, $t2
		move $t3, $t4
		li $t4, 1
		beq $t7, $t4, main_label2
		addi $t1, $t1, 6
		j main_loop
main_exit:
		move $t0, $t0
		move $t4, $t6
		li $t4, 1
		move $t3, $t4
main_print:
		li $v0, 1
		move $a0, $t3
		syscall
		li $v0, 11
		li $a0, 10
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

