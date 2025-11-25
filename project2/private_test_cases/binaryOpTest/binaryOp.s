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
		li $v0, 5
		syscall
		move $t1, $v0
		li $v0, 5
		syscall
		move $t0, $v0
		add $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		sub $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		mul $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		div $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		and $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
		or $t2, $t1, $t0
		li $v0, 1
		move $a0, $t2
		syscall
		li $v0, 11
		li $a0, 10
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

