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
		addi $sp, $fp, -16
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 12($sp)
		li $t0, 0
		sw $t0, 8($sp)
		li $t0, 0
		sw $t0, 4($sp)
		li $t0, 0
		sw $t0, 0($sp)
		li $v0, 1
		lw $t0, 8($sp)
		move $a0, $t0
		syscall
main_lstart:
		li $t0, 1
		sw $t0, -4($sp)
		lw $t0, -4($sp)
		lw $t1, 4($sp)
		bge $t0, $t1, main_lexit
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 4($sp)
		li $t0, 5
		sw $t0, -4($sp)
		lw $t0, 12($sp)
		lw $t1, -4($sp)
		mul $t0, $t0, $t1
		sw $t0, 12($sp)
		j main_lstart
main_lexit:
		li $v0, 1
		lw $t0, 4($sp)
		move $a0, $t0
		syscall
		li $t0, 0
		sw $t0, 0($sp)
		li $t0, 0
		sw $t0, -4($sp)
		lw $t0, -4($sp)
		lw $t1, 12($sp)
		bge $t0, $t1, main_else1
		li $t0, 0
		sw $t0, 0($sp)
main_else1:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

