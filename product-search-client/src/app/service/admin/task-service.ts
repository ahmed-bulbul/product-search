import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Task } from '../../models/task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private http = inject(HttpClient); // ✅ Use Angular's inject()

  private baseUrl = 'http://localhost:8080/api/v1/tasks';

  constructor() { }

  create(task: Task): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, task);  // Returns Observable<Task>
  }

  getAll() {
    return this.http.get<any[]>(this.baseUrl+'/jobs');
  }

  deleteJob(id: string) {
    const url = this.baseUrl + '/' + id;
    return this.http.delete(url); 
  }

}
