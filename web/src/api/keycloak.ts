// @ts-ignore
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
    url: import.meta.env.VITE_KEYCLOAK_URL,
    realm: import.meta.env.VITE_KEYCLOAK_REALM,
    scope: 'openId'
});

export default keycloak;
