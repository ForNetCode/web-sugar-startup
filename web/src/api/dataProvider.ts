import axios from "axios";
import log from 'loglevel';
import keycloak from './keycloak'

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use(
    async (config) => {
        const token = keycloak.sessionId;
        if (token && !config.headers.Authorization) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);

export function registerSession() {
    axiosInstance.get( import.meta.env.VITE_API_URL + '/auth/session', {
        headers: {
            Authorization: `Bearer ${keycloak.token}`
        }
    }).then((resp) => {
        log.info("Token Refreshed")
    }).catch((error) => {
        log.error("Token refresh fail", error)
    })
}


export default axiosInstance
