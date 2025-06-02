import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TaskService } from '../../service/admin/task-service';
import { Task } from '../../models/task';

@Component({
  selector: 'app-scheduler-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './scheduler-component.html',
  styleUrl: './scheduler-component.css'
})
export class SchedulerComponent {

  task: Task = {
    id:'',
    name: '',
    group: '',
    cronExpression: '',
    description: '',
    triggerDay: 0
  };

constructor(private taskService: TaskService, private router: Router) {}
onSubmit(): void {
  const { name, group, cronExpression } = this.task;

  if (name && group && cronExpression) {
    this.taskService.create(this.task).subscribe({
      next: () => {
        alert('Task created successfully!');
        this.router.navigate(['/admin/jobs']);
      },
      error: (err: any) => {
        console.error('Error creating task:', err);
        alert('Failed to create task.');
      }
    });
  } else {
    alert('All fields are required.');
  }
  
}
}
