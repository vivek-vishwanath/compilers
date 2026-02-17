/*
 * OS Simulator - Intense Function Call Profile Test
 * This simulates OS kernel operations with deep call chains,
 * interrupt handling, process scheduling, and memory management.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

/* ===== Type Definitions ===== */
typedef struct {
    int pid;
    int priority;
    int quantum;
    int state;
} process_t;

typedef struct {
    void* addr;
    size_t size;
    int flags;
} mem_block_t;

/* ===== Low-level Hardware Abstraction ===== */
static inline void outb(unsigned short port, unsigned char val) {
    /* Simulated I/O */
}

static inline unsigned char inb(unsigned short port) {
    return (unsigned char)(port & 0xFF);
}

static inline void cli() {
    /* Disable interrupts */
}

static inline void sti() {
    /* Enable interrupts */
}

/* ===== Memory Management Layer ===== */
void* kmalloc_internal(size_t size, int flags) {
    return malloc(size);
}

void kfree_internal(void* ptr) {
    free(ptr);
}

void* page_alloc_low(int order) {
    size_t size = 4096 << order;
    return kmalloc_internal(size, 0);
}

void page_free_low(void* addr, int order) {
    kfree_internal(addr);
}

void* page_alloc(int order) {
    void* page = page_alloc_low(order);
    if (page) {
        memset(page, 0, 4096 << order);
    }
    return page;
}

void page_free(void* addr, int order) {
    if (addr) {
        page_free_low(addr, order);
    }
}

void* vmalloc_node(size_t size, int node) {
    int order = 0;
    size_t pages = (size + 4095) / 4096;
    while ((1U << order) < pages) order++;
    return page_alloc(order);
}

void vfree_node(void* addr, int node) {
    page_free(addr, 0);
}

void* vmalloc(size_t size) {
    return vmalloc_node(size, 0);
}

void vfree(void* addr) {
    vfree_node(addr, 0);
}

void* kmalloc(size_t size, int flags) {
    if (size > 4096) {
        return vmalloc(size);
    }
    return kmalloc_internal(size, flags);
}

void kfree(void* ptr) {
    if (ptr) {
        kfree_internal(ptr);
    }
}

/* ===== Locking Primitives ===== */
void spin_lock_irqsave(void* lock, unsigned long* flags) {
    cli();
}

void spin_unlock_irqrestore(void* lock, unsigned long flags) {
    sti();
}

void mutex_lock(void* mutex) {
    /* Simulated mutex lock */
}

void mutex_unlock(void* mutex) {
    /* Simulated mutex unlock */
}

/* ===== Interrupt Handling ===== */
void ack_irq(int irq) {
    outb(0x20, 0x20);
}

void mask_irq(int irq) {
    unsigned char mask = inb(0x21);
    mask |= (1 << irq);
    outb(0x21, mask);
}

void unmask_irq(int irq) {
    unsigned char mask = inb(0x21);
    mask &= ~(1 << irq);
    outb(0x21, mask);
}

void handle_irq_event(int irq) {
    ack_irq(irq);
}

void do_IRQ_handler(int irq) {
    handle_irq_event(irq);
    unmask_irq(irq);
}

void do_IRQ(int irq) {
    mask_irq(irq);
    do_IRQ_handler(irq);
}

void timer_interrupt_handler() {
    do_IRQ(0);
}

void keyboard_interrupt_handler() {
    do_IRQ(1);
}

/* ===== Process Scheduling ===== */
process_t* find_next_task_rr(process_t* tasks, int count) {
    static int idx = 0;
    idx = (idx + 1) % count;
    return &tasks[idx];
}

process_t* find_next_task_priority(process_t* tasks, int count) {
    process_t* best = &tasks[0];
    for (int i = 1; i < count; i++) {
        if (tasks[i].priority > best->priority) {
            best = &tasks[i];
        }
    }
    return best;
}

process_t* select_next_task(process_t* tasks, int count, int policy) {
    if (policy == 0) {
        return find_next_task_rr(tasks, count);
    } else {
        return find_next_task_priority(tasks, count);
    }
}

void context_switch_low(process_t* prev, process_t* next) {
    /* Save prev state, load next state */
}

void context_switch(process_t* prev, process_t* next) {
    cli();
    context_switch_low(prev, next);
    sti();
}

void schedule_tail(process_t* prev) {
    sti();
}

void __schedule(process_t* tasks, int count) {
    static int current = 0;
    process_t* prev = &tasks[current];
    process_t* next = select_next_task(tasks, count, 0);

    if (prev != next) {
        current = next - tasks;
        context_switch(prev, next);
        schedule_tail(prev);
    }
}

void schedule() {
    static process_t tasks[4] = {
        {1, 10, 100, 1},
        {2, 5, 100, 1},
        {3, 8, 100, 1},
        {4, 3, 100, 1}
    };
    __schedule(tasks, 4);
}

void preempt_schedule() {
    cli();
    schedule();
    sti();
}

void yield() {
    preempt_schedule();
}

/* ===== File System Layer ===== */
int vfs_open_low(const char* path, int flags) {
    return 3; /* Simulated fd */
}

int vfs_open(const char* path, int flags) {
    int fd = vfs_open_low(path, flags);
    return fd;
}

int sys_open(const char* path, int flags) {
    return vfs_open(path, flags);
}

int vfs_read_low(int fd, void* buf, size_t count) {
    return count; /* Simulated read */
}

int vfs_read(int fd, void* buf, size_t count) {
    return vfs_read_low(fd, buf, count);
}

int sys_read(int fd, void* buf, size_t count) {
    return vfs_read(fd, buf, count);
}

int vfs_write_low(int fd, const void* buf, size_t count) {
    return count; /* Simulated write */
}

int vfs_write(int fd, const void* buf, size_t count) {
    return vfs_write_low(fd, buf, count);
}

int sys_write(int fd, const void* buf, size_t count) {
    return vfs_write(fd, buf, count);
}

void vfs_close_low(int fd) {
    /* Simulated close */
}

void vfs_close(int fd) {
    vfs_close_low(fd);
}

void sys_close(int fd) {
    vfs_close(fd);
}

/* ===== Network Stack ===== */
void eth_send_frame_low(const void* data, size_t len) {
    /* Simulated ethernet send */
}

void eth_send_frame(const void* data, size_t len) {
    eth_send_frame_low(data, len);
}

void ip_send_packet_low(const void* data, size_t len, unsigned int dest) {
    eth_send_frame(data, len);
}

void ip_send_packet(const void* data, size_t len, unsigned int dest) {
    ip_send_packet_low(data, len, dest);
}

void tcp_send_segment_low(const void* data, size_t len, int sock) {
    ip_send_packet(data, len, 0xC0A80001); /* 192.168.0.1 */
}

void tcp_send_segment(const void* data, size_t len, int sock) {
    tcp_send_segment_low(data, len, sock);
}

void tcp_send(int sock, const void* data, size_t len) {
    tcp_send_segment(data, len, sock);
}

void socket_send(int sock, const void* data, size_t len) {
    tcp_send(sock, data, len);
}

void sys_sendto(int sock, const void* data, size_t len, int flags) {
    socket_send(sock, data, len);
}

void eth_recv_frame_low(void* data, size_t* len) {
    *len = 0;
}

void eth_recv_frame(void* data, size_t* len) {
    eth_recv_frame_low(data, len);
}

void ip_recv_packet_low(void* data, size_t* len) {
    eth_recv_frame(data, len);
}

void ip_recv_packet(void* data, size_t* len) {
    ip_recv_packet_low(data, len);
}

void tcp_recv_segment_low(void* data, size_t* len, int sock) {
    ip_recv_packet(data, len);
}

void tcp_recv_segment(void* data, size_t* len, int sock) {
    tcp_recv_segment_low(data, len, sock);
}

void tcp_recv(int sock, void* data, size_t* len) {
    tcp_recv_segment(data, len, sock);
}

void socket_recv(int sock, void* data, size_t* len) {
    tcp_recv(sock, data, len);
}

void sys_recvfrom(int sock, void* data, size_t* len, int flags) {
    socket_recv(sock, data, len);
}

/* ===== Device Drivers ===== */
void device_init_low(int dev_id) {
    outb(0x3F8 + dev_id, 0x01);
}

void device_init(int dev_id) {
    device_init_low(dev_id);
}

void device_probe_low(int dev_id) {
    inb(0x3F8 + dev_id);
}

void device_probe(int dev_id) {
    device_probe_low(dev_id);
    device_init(dev_id);
}

void driver_register_low(int drv_id) {
    /* Register driver */
}

void driver_register(int drv_id) {
    driver_register_low(drv_id);
}

void pci_driver_probe(int drv_id) {
    driver_register(drv_id);
    for (int i = 0; i < 4; i++) {
        device_probe(i);
    }
}

void platform_driver_probe(int drv_id) {
    driver_register(drv_id);
    for (int i = 0; i < 2; i++) {
        device_probe(i);
    }
}

void bus_probe_devices() {
    pci_driver_probe(1);
    platform_driver_probe(2);
}

/* ===== System Call Interface ===== */
void syscall_entry_low(int nr) {
    /* Entry point */
}

void syscall_entry(int nr) {
    syscall_entry_low(nr);
}

long do_syscall(int nr, long arg1, long arg2, long arg3) {
    syscall_entry(nr);

    switch (nr) {
        case 0: /* read */
            return sys_read((int)arg1, (void*)arg2, (size_t)arg3);
        case 1: /* write */
            return sys_write((int)arg1, (const void*)arg2, (size_t)arg3);
        case 2: /* open */
            return sys_open((const char*)arg1, (int)arg2);
        case 3: /* close */
            sys_close((int)arg1);
            return 0;
        default:
            return -1;
    }
}

/* ===== Complex Workflow Simulation ===== */
void complex_workflow_1() {
    void* mem1 = kmalloc(1024, 0);
    void* mem2 = kmalloc(2048, 0);

    int fd = sys_open("/dev/null", 0);
    sys_write(fd, mem1, 1024);
    sys_read(fd, mem2, 2048);
    sys_close(fd);

    kfree(mem1);
    kfree(mem2);

    yield();
}

void complex_workflow_2() {
    for (int i = 0; i < 3; i++) {
        void* page = page_alloc(0);
        timer_interrupt_handler();
        page_free(page, 0);
        keyboard_interrupt_handler();
    }
    schedule();
}

void complex_workflow_3() {
    char buf[256];
    size_t len;

    sys_sendto(1, "test", 4, 0);
    sys_recvfrom(1, buf, &len, 0);
    sys_sendto(1, buf, len, 0);

    yield();
}

void complex_workflow_4() {
    void* mem = vmalloc(8192);

    int fd1 = sys_open("/tmp/test1", 0);
    int fd2 = sys_open("/tmp/test2", 0);

    sys_write(fd1, mem, 4096);
    sys_write(fd2, mem + 4096, 4096);

    sys_close(fd1);
    sys_close(fd2);

    vfree(mem);
}

void run_complex_workflows() {
    for (int i = 0; i < 5; i++) {
        complex_workflow_1();
        complex_workflow_2();
        complex_workflow_3();
        complex_workflow_4();
    }
}

/* ===== Main Simulation ===== */
void kernel_init() {
    printf("Initializing kernel subsystems...\n");
    bus_probe_devices();
    printf("Device initialization complete\n");
}

void kernel_run() {
    printf("Running kernel workflows...\n");

    for (int cycle = 0; cycle < 10; cycle++) {
        timer_interrupt_handler();
        run_complex_workflows();
        schedule();

        if (cycle % 3 == 0) {
            keyboard_interrupt_handler();
        }
    }

    printf("Kernel workflows complete\n");
}

int main() {
    printf("OS Simulator - Intense Function Call Profile Test\n");
    printf("==================================================\n\n");

    kernel_init();
    kernel_run();

    printf("\nSimulation complete.\n");
    return 0;
}