const adsList = document.getElementById("adsList");
const loginBtn = document.getElementById("loginBtn");
const logoutBtn = document.getElementById("logoutBtn");
const postAdBtn = document.getElementById("postAdBtn"); // ✅ New "Post Ad" button
const newAdSection = document.getElementById("newAdSection");
const newAdForm = document.getElementById("newAdForm");

const editModal = new bootstrap.Modal(document.getElementById("editAdModal"));
const editForm = document.getElementById("editAdForm");

let currentUser = null;
let allAds = [];
let editingAdId = null;

// ✅ Load Ads
async function loadAds() {
  const res = await fetch("/api/ads", { credentials: "include" });
  allAds = await res.json();

  adsList.innerHTML = "";
  allAds.forEach(ad => {
    const adId = ad.id || ad._id;

    const card = document.createElement("div");
    card.className = "col-md-4 col-12 mb-3";
    card.innerHTML = `
      <div class="card shadow-sm">
        ${ad.images?.length > 0 ? `<img src="data:image/png;base64,${ad.images[0]}" class="card-img-top">` : ""}
        <div class="card-body">
          <h5 class="card-title">${ad.location} - ₹${ad.amount}</h5>
          <p class="card-text">${ad.description}</p>
          <span class="badge bg-info">${ad.type}</span>

          ${
            currentUser && (currentUser.role === "ADMIN" || currentUser.email === ad.userEmail)
              ? `<div class="mt-3 d-flex gap-2">
                   <button class="btn btn-sm btn-outline-primary" onclick="openEditAd('${adId}')">✏ Edit</button>
                   <button class="btn btn-sm btn-outline-danger" onclick="deleteAd('${adId}')">🗑 Delete</button>
                 </div>`
              : ""
          }
        </div>
      </div>`;
    adsList.appendChild(card);
  });
}

// ✅ Delete Ad
async function deleteAd(adId) {
  if (!confirm("Are you sure you want to delete this ad?")) return;
  const res = await fetch(`/api/ads/${adId}`, { method: "DELETE", credentials: "include" });
  if (res.ok) {
    alert("✅ Ad deleted!");
    loadAds();
  } else {
    alert("❌ Failed to delete ad");
  }
}

// ✅ Open Edit Modal
function openEditAd(adId) {
  const ad = allAds.find(a => (a.id || a._id) === adId);
  if (!ad) return alert("Ad not found!");

  editingAdId = adId;

  // Pre-fill modal
  document.getElementById("editLocation").value = ad.location;
  document.getElementById("editAmount").value = ad.amount;
  document.getElementById("editDescription").value = ad.description;
  document.getElementById("editType").value = ad.type;

  editModal.show();
}

// ✅ Submit Edit
editForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const ad = allAds.find(a => (a.id || a._id) === editingAdId);
  const updatedAd = {
    ...ad,
    location: document.getElementById("editLocation").value,
    amount: parseInt(document.getElementById("editAmount").value),
    description: document.getElementById("editDescription").value,
    type: document.getElementById("editType").value,
    images: ad.images // ✅ keep existing images
  };

  const res = await fetch(`/api/ads/${editingAdId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(updatedAd)
  });

  if (res.ok) {
    alert("✅ Ad updated!");
    editModal.hide();
    loadAds();
  } else {
    alert("❌ Failed to update ad");
  }
});

// ✅ Check login
async function checkLogin() {
  const res = await fetch("/api/user/me", { credentials: "include" });
  if (!res.ok) {
    currentUser = null;
    loginBtn.classList.remove("d-none");
    logoutBtn.classList.add("d-none");
    postAdBtn.classList.add("d-none");
    return;
  }
  currentUser = await res.json();
  loginBtn.classList.add("d-none");
  logoutBtn.classList.remove("d-none");
  postAdBtn.classList.remove("d-none");
}

// ✅ Login / Logout
loginBtn.onclick = () => (window.location.href = "/oauth2/authorization/google");
logoutBtn.onclick = async () => {
  await fetch("/logout", { credentials: "include" });
  currentUser = null;
  checkLogin();
  loadAds();
};

// ✅ Show Post Ad section
postAdBtn.onclick = () => {
  newAdSection.classList.toggle("d-none");
};

// ✅ Post new ad
newAdForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const file = document.getElementById("imageInput").files[0];
  let base64Img = "";
  if (file) base64Img = await toBase64(file);

  const adData = {
    location: document.getElementById("location").value,
    amount: parseInt(document.getElementById("amount").value),
    type: document.getElementById("type").value,
    description: document.getElementById("description").value,
    images: base64Img ? [base64Img] : []
  };

  const res = await fetch("/api/ads", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(adData),
    credentials: "include"
  });

  if (res.ok) {
    alert("✅ Ad posted!");
    newAdSection.classList.add("d-none"); // ✅ hide form after posting
    loadAds();
  } else {
    alert("❌ Failed to post ad");
  }
});

// ✅ Helper: Convert file → Base64
function toBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result.split(",")[1]);
    reader.onerror = reject;
  });
}

// ✅ Init
(async function init() {
  await checkLogin();
  await loadAds();
})();
