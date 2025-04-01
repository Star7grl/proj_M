//config
import RegisterPage from "../pages/RegisterPage";
import HomePage from "../pages/HomePage";
import LoginPage from "../pages/LoginPage"; 
import ServicesPage from "../pages/ServicesPage"; 
import AdminPanelPage from "../pages/AdminPanelPage"; // Новое: импорт админ-панели
import ProfilePage from "../pages/ProfilePage";


export const publicRoutes= [
    {
        title:"Главная",
        path: "/",
        page: HomePage
    },
    {
        title:"Услуги",
        path: "/services",
        page: ServicesPage
    },
    {
        title:"Номера",
        path: "/rooms",
        page: RoomsPage
    }, {
        title:"Контакты",
        path: "/contacts",
        page: ContactsPagePage
    }
];

export const authRoutes= [
    {
        title:"Регистрация",
        path: "/register",
        page: RegisterPage
    },
    {
        title:"Вход",
        path: "/login",
        page: LoginPage
    }
];

export const privateRoutes= [
    {
        title:"Профиль",
        path: "/profile",
        page: ProfilePage
    },
    {
        title: "Админ-панель",
        path: "/admin",
        page: AdminPanelPage
    }
];