export const AppState = {
  currentUser: null,
  currentDetailPhone: null,
};

export const ProductsState = {
  rawItems: [],
  items: [],
  currentPage: 1,
  pageSize: 8,
  brandId: "",
  keyword: "",
  priceFilter: "",
  sort: "",
};

export const CartState = { cart: null };

export const PaymentState = { methods: [] };
