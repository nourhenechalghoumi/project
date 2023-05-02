import { CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { OrderService } from '../../Service/order.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component1.html',
  styleUrls: ['./orders.component1.css'],
  providers: [CurrencyPipe]
})
export class Order1Component {
  selectedProducts: any[] = [];
  totalPrice!: number ;
  successMessage: string = '';
  productId!: number;
  quantity!: number;
  orderForm: FormGroup | undefined;

  constructor(
    private orderService: OrderService,
    private FormBuilder : FormBuilder) {}

  addProduct() {
    let existingProduct = this.selectedProducts.find(p => p.id === this.productId);
    if (existingProduct) {
      existingProduct.quantity += this.quantity;
    } else {
      this.selectedProducts.push({id: this.productId, name: '', price: 0, quantity: this.quantity});
    }
    this.calculateTotalPrice();
  }

  removeProduct(product: any) {
    let index = this.selectedProducts.findIndex(p => p.id === product.id);
    if (index !== -1) {
      if (this.selectedProducts[index].quantity > 1) {
        this.selectedProducts[index].quantity -= 1;
      } else {
        this.selectedProducts.splice(index, 1);
      }
    }
    this.calculateTotalPrice();
  }

  calculateTotalPrice() {
    this.totalPrice = this.selectedProducts.reduce((total, product) => total + (product.price * product.quantity), 0);
  }

  submitOrder() {
    let orderItems = this.selectedProducts.map(product => ({productId: product.id, quantity: product.quantity}));
    let createOrderDto = {userId: 1, orderItems: orderItems};
    this.orderService.createOrder(createOrderDto).subscribe(order => {
      this.successMessage = `Order created successfully with total price of $${this.totalPrice}`;
      this.selectedProducts = [];
      this.totalPrice = 0;
      alert(this.successMessage);
    });
  }
}
