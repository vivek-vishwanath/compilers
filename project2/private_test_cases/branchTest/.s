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
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 4($sp)
		li $v0, 5
		syscall
		move $t0, $v0
		sw $t0, 0($sp)
main_breq:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		beq $t0, $t1, main_took_breq
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		beq $t0, $t1, main_took_breq
		j main_not_taken_breq
main_took_breq:
		li $v0, 11
		li $a0, 84
		syscall
		j main_brneq_case
main_not_taken_breq:
		li $v0, 11
		li $a0, 78
		syscall
main_brneq_case:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bne $t0, $t1, main_took_brneq
		j main_not_taken_brneq
main_took_brneq:
		li $v0, 11
		li $a0, 84
		syscall
		j main_brlt_case
main_not_taken_brneq:
		li $v0, 11
		li $a0, 78
		syscall
main_brlt_case:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		blt $t0, $t1, main_took_brlt
		j main_not_taken_brlt
main_took_brlt:
		li $v0, 11
		li $a0, 84
		syscall
		j main_brgt_case
main_not_taken_brlt:
		li $v0, 11
		li $a0, 78
		syscall
main_brgt_case:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bgt $t0, $t1, main_took_brgt
		j main_not_taken_brgt
main_took_brgt:
		li $v0, 11
		li $a0, 84
		syscall
		j main_brgeq_case
main_not_taken_brgt:
		li $v0, 11
		li $a0, 78
		syscall
main_brgeq_case:
		lw $t0, 4($sp)
		lw $t1, 0($sp)
		bge $t0, $t1, main_took_brgeq
		j main_not_taken_brgeq
main_took_brgeq:
		li $v0, 11
		li $a0, 84
		syscall
		j main_end
main_not_taken_brgeq:
		li $v0, 11
		li $a0, 78
		syscall
main_end:
		add $zero, $zero, $zero
main_teardown:
		addi $sp, $fp, 8
		lw $ra, 4($fp)
		lw $fp, 0($fp)
		jr $ra

