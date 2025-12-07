const API_BASE_URL = "";
function loadPhones() {
    document.getElementById("title").innerText = "Quản lý Sản phẩm";

    fetch(`${API}/phones`)
        .then(res => res.json())
        .then(data => {
            let html = `
                <button onclick="addPhoneForm()">+ Thêm sản phẩm</button>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Tên</th>
                        <th>Giá</th>
                        <th>Hình ảnh</th>
                        <th>Thương hiệu</th>
                        <th>Hành động</th>
                    </tr>
            `;

            data.forEach(p => {
                html += `
                    <tr>
                        <td>${p.phoneId}</td>
                        <td>${p.phoneName}</td>
                        <td>${p.price}</td>
                        <td><img src="${p.coverImageURL}" width="60"></td>
                        <td>${p.brandName}</td>
                        <td>
                            <button onclick="editPhone(${p.phoneId})">Sửa</button>
                            <button onclick="deletePhone(${p.phoneId})">Xóa</button>
                        </td>
                    </tr>
                `;
            });

            html += "</table>";
            document.getElementById("main").innerHTML = html;
        });
}
