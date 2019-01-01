package com.geckostroll.common.bizflow;

/**
 *
 * @author yanhuai
 * @version $Id: SingletonProcessor.java, v 0.1 2019年01月01日 19:56 yanhuai Exp $
 */
public enum SingletonProcessor implements IBizProcess {

    /** starter */
    STARTER {
        /**
         * get id.
         *
         * @return
         */
        public String getId() {
            return "STARTER";
        }

        /**
         * whether on process.
         *
         * @param ctx
         * @return
         */
        public boolean onProcess(IBizProcContext ctx) {
            return true;
        }

        /**
         * get fake response.
         *
         * @param ctx
         * @return
         */
        public Object getFakeResponse(IBizProcContext ctx) {
            return null;
        }

    }

    ;

    /**
     * Get starter
     *
     * @return
     */
    public static IBizProcess getStarter() {
        return STARTER;
    }
}
