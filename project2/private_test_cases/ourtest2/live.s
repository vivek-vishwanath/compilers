.data
	STACK: .word -2147483648

.text
	lw $sp, STACK
	move $fp, $sp
	jal main
	li $v0, 10
	syscall

live:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -20
		move $t2, $a0
		li $t2, 10
		li $t3, 0
		li $t2, 1
live_loop:
		blt $t3, $t2, live_ret
		li $t3, 2
		mul $t5, $t0, $t3
		add $t3, $t5, $t2
		addi $t0, $t0, 1
		add $t2, $t0, $t3
		j live_loop
live_ret:
		move $v0, $t2
		j live_teardown
live_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

