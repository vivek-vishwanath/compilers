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
		addi $sp, $fp, -16
		li $v0, 5
		syscall
		move $t0, $v0
		li $v0, 5
		syscall
		move $t2, $v0
		li $t1, 0
		li $t3, 0
		or $t1, $t0, $t2
		and $t1, $t0, $t2
		and $t3, $t0, $t2
		or $t3, $t0, $t2
		li $v0, 1
		move $a0, $t1
		syscall
		li $v0, 1
		move $a0, $t3
		syscall
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

