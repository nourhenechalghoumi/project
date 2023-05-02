export interface CreateorderDto {
    userId: number;
  orderItems: { productId: number, quantity: number }[];
}

