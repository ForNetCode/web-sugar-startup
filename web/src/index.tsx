import { createRoot } from "react-dom/client";
import log from 'loglevel'
import prefixer from 'loglevel-plugin-prefix'
import { ReactKeycloakProvider } from "@react-keycloak/web";

import App from "./App";
import keycloak from "./api/keycloak";

const container = document.getElementById("root") as HTMLElement;
const root = createRoot(container);

log.setLevel(import.meta.env.PROD ? 'info':'debug')
prefixer.reg(log)
prefixer.apply(log)
log.debug('begin to init React')

root.render(
  <ReactKeycloakProvider authClient={keycloak}>
    <App />
  </ReactKeycloakProvider>
);
