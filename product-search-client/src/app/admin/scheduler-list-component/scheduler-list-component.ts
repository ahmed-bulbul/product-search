import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Task } from '../../models/task';
import { TaskService } from '../../service/admin/task-service';
import { BehaviorSubject, Observable } from 'rxjs';

@Component({
  selector: 'app-scheduler-list-component',
  imports: [RouterModule, CommonModule],
  templateUrl: './scheduler-list-component.html',
  styleUrl: './scheduler-list-component.css'
})
export class SchedulerListComponent implements OnInit {
  constructor(private http: HttpClient) {}

  private taskService = inject(TaskService);
  
  // Create a subject to store and update tasks
  private tasksSubject = new BehaviorSubject<Task[]>([]);
  
  // Expose the observable for the template to consume
  tasks$ = this.tasksSubject.asObservable();

  ngOnInit(): void {
    this.refreshTasks(); // This is all you need for initial load
  }

  refreshTasks(): void {
    this.taskService.getAll().subscribe(tasks => {
      this.tasksSubject.next(tasks);
    });
  }

  deleteJob(id: string) {
    this.taskService.deleteJob(id).subscribe(() => {
      this.refreshTasks(); // Refresh after delete
    });
  }
}
