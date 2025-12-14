const API = "/api";


/* ========== TAB LOGIC ========== */
function showTab(tab) {
    document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
    document.querySelectorAll(".tab-page").forEach(p => p.classList.remove("active"));

    document.querySelector(`button[onclick="showTab('${tab}')"]`).classList.add("active");
    document.getElementById(tab).classList.add("active");
}

/* LOAD DASHBOARD */
window.onload = function () {
    // Load overview
    loadSummary();
    loadOverviewChart();

    // Load filters & advanced charts
    loadFilters();
    loadAdvancedCharts();
};

/* ====== OVERVIEW ====== */
function loadSummary() {
    fetch(`${API}/dashboard/summary`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("totalProducts").innerHTML = data.totalProducts;
            document.getElementById("totalOrders").innerHTML = data.totalOrders;
            document.getElementById("totalUsers").innerHTML = data.totalUsers;
            document.getElementById("monthRevenue").innerHTML =
                data.monthRevenue.toLocaleString() + " đ";
        })
        .catch(err => {
            console.error("Lỗi load summary:", err);
        });
}


function loadOverviewChart() {
    fetch(`${API}/dashboard/revenue-monthly`)
    .then(res => res.json())
    .then(data => {
        const ctx = document.getElementById("revenueOverviewChart");
        new Chart(ctx, {
            type: "line",
            data: {
                labels: data.map(x => x.month),
                datasets: [{
                    label: "Doanh thu",
                    data: data.map(x => x.total),
                    borderColor: "#1e88e5",
                    borderWidth: 3,
                    tension: 0.4,
                    backgroundColor: "rgba(30,136,229,0.1)"
                }]
            }
        });
    });
}

/* ====== ADVANCED FILTERS ====== */
function loadFilters() {
    for (let i = 1; i <= 12; i++)
        monthFilter.innerHTML += `<option value="${i}">${i}</option>`;

    for (let y = 2020; y <= 2035; y++)
        yearFilter.innerHTML += `<option value="${y}">${y}</option>`;

    fetch(`${API}/brands`)
    .then(res => res.json())
    .then(data => {
        data.forEach(b => brandFilter.innerHTML += `<option value="${b.brandId}">${b.brandName}</option>`);
    });
}

function applyFilters() {
    loadAdvancedCharts();
}

/* ====== ADVANCED CHARTS ====== */
function loadAdvancedCharts() {
    loadComboRevenue();
    loadTopProducts();
    loadBrandDonut();
}

function loadComboRevenue() {
    let url = `${API}/dashboard/revenue?`;
    if (monthFilter.value) url += `month=${monthFilter.value}&`;
    if (yearFilter.value) url += `year=${yearFilter.value}&`;
    if (brandFilter.value) url += `brandId=${brandFilter.value}`;

    fetch(url)
    .then(res => res.json())
    .then(data => {
        const ctx = document.getElementById("comboRevenueChart");

        if (window.comboChart) window.comboChart.destroy();

        window.comboChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: data.map(x => x.date),
                datasets: [
                    {
                        type: "line",
                        label: "Line",
                        data: data.map(x => x.total),
                        borderColor: "#ef5350",
                        borderWidth: 3,
                        tension: 0.4
                    },
                    {
                        type: "bar",
                        label: "Bar",
                        data: data.map(x => x.total),
                        backgroundColor: "#1e88e5"
                    }
                ]
            }
        });
    });
}

function loadTopProducts() {
    fetch(`${API}/dashboard/top-products`)
    .then(res => res.json())
    .then(data => {
        const ctx = document.getElementById("topProductChart");

        if (window.topChart) window.topChart.destroy();

        window.topChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: data.map(x => x.phoneName),
                datasets: [{
                    label: "SL bán",
                    data: data.map(x => x.totalSold),
                    backgroundColor: "#8e44ad"
                }]
            }
        });
    });
}

function loadBrandDonut() {
    fetch(`${API}/dashboard/brand-sales`)
    .then(res => res.json())
    .then(data => {
        const ctx = document.getElementById("brandDonutChart");

        if (window.donutChart) window.donutChart.destroy();

        window.donutChart = new Chart(ctx, {
            type: "doughnut",
            data: {
                labels: data.map(x => x.brandName),
                datasets: [{
                    data: data.map(x => x.total),
                    backgroundColor: ["#1abc9c", "#3498db", "#9b59b6", "#f1c40f", "#e74c3c"]
                }]
            }
        });
    });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
