// @ts-ignore
import Keycloak from 'keycloak-js';
import {registerSession} from './dataProvider'

const keycloak = new Keycloak({
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
    url: import.meta.env.VITE_KEYCLOAK_URL,
    realm: import.meta.env.VITE_KEYCLOAK_REALM,
    // scope: 'openId',
    async onAuthRefreshSuccess() {
        registerSession()
    }
});

export default keycloak;
