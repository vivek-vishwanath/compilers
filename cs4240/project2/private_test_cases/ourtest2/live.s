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

live:
		sw $fp, -8($sp)
		addi $fp, $sp, -8
		sw $ra, 4($fp)
		addi $sp, $fp, -20
		move $t1, $a0
		li $t2, 10
		li $t3, 0
		li $t0, 1
live_loop:
		blt $t3, $t1, live_ret
		li $t2, 2
		mul $t4, $t3, $t2
		add $t2, $t4, $t0
		addi $t3, $t3, 1
		add $t0, $t3, $t2
		j live_loop
live_ret:
		move $v0, $t0
		j live_teardown
live_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

