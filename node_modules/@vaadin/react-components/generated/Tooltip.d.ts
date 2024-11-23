import { Tooltip as TooltipElement } from "@vaadin/tooltip/vaadin-tooltip.js";
import * as React from "react";
import { type WebComponentProps } from "../utils/createComponent.js";
export * from "@vaadin/tooltip/vaadin-tooltip.js";
export { TooltipElement, };
export type TooltipEventMap = Readonly<{}>;
export type TooltipProps = WebComponentProps<TooltipElement, TooltipEventMap>;
export declare const Tooltip: (props: Partial<import("../utils/createComponent.js").ThemedWebComponentProps<TooltipElement, Readonly<{}>>> & React.RefAttributes<TooltipElement>) => React.ReactElement | null;
//# sourceMappingURL=Tooltip.d.ts.map