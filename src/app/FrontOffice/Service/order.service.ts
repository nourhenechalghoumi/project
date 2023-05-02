import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateorderDto } from 'src/app/Modules/createorder-dto';
import { OrderViewDto } from 'src/app/Modules/order-view-dto';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/orders';

  constructor(private http: HttpClient) { }

  createOrder(createOrderDto: CreateorderDto): Observable<OrderViewDto> {
    return this.http.post<OrderViewDto>(`${this.apiUrl}/create`, createOrderDto);
  }
}


